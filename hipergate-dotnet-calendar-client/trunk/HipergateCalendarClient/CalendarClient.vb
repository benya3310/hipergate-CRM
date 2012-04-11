' Copyright (C) 2010  Know Gate S.L. All rights reserved.
' 
' Redistribution and use in source and binary forms, with or without
' modification, are permitted provided that the following conditions
' are met:
' 
' 1. Redistributions of source code must retain the above copyright
'    notice, this list of conditions and the following disclaimer.
' 
' 2. The end-user documentation included with the redistribution,
'    if any, must include the following acknowledgment:
'    "This product includes software parts from hipergate
'    (http://www.hipergate.org/)."
'    Alternately, this acknowledgment may appear in the software itself,
'    if and wherever such third-party acknowledgments normally appear.
' 
' 3. The name hipergate must not be used to endorse or promote products
'    derived from this software without prior written permission.
'    Products derived from this software may not be called hipergate,
'    nor may hipergate appear in their name, without prior written
'    permission.
' 
' This library is distributed in the hope that it will be useful,
' but WITHOUT ANY WARRANTY; without even the implied warranty of
' MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
' 
' You should have received a copy of hipergate License with this code;
' if not, visit http://www.hipergate.org or mail to info@hipergate.org

Public Class CalendarClient
    Private sSecurityToken As String = ""
    Private iErrCode As Int32
    Private sErrMsg As String
    Private sSrvUrl As String

    Protected Overrides Sub Finalize()
        If Len(sSecurityToken) > 0 Then
            Try
                Disconnect()
            Catch ex As Exception
                ' Ignore any disconnection exception whilst destroying object instance
            End Try
        End If
    End Sub

    ''' <summary>Error code number for last operation</summary>
    ''' <returns>Zero if last operation was successfull</returns>
    Public ReadOnly Property errorCode() As String
        Get
            Return iErrCode
        End Get
    End Property

    ''' <summary>Error message for last operation</summary>
    ''' <returns>Empty String if last operation was successfull</returns>
    ''' <remarks></remarks>
    Public ReadOnly Property errorMessage() As String
        Get
            Return sErrMsg
        End Get
    End Property

    ''' <summary>Connect to calendar service</summary>
    ''' <param name="sServiceUrl">http://hostname.com/hipergate/servlet/HttpCalendarServlet</param>
    ''' <param name="sUserEmail">user@hipergate.org</param>
    ''' <param name="sPassword">******</param>
    ''' <returns>True if connection was sucessfull</returns>
    ''' <exception cref="System.Net.WebException"></exception>
    Public Function Connect(ByVal sServiceUrl As String, ByVal sUserEmail As String, ByVal sPassword As String) As Boolean

        Dim oReq As System.Net.HttpWebRequest = System.Net.HttpWebRequest.Create(sServiceUrl + "?command=connect&user=" + sUserEmail + "&password=" + sPassword)
        Dim oRdr As New System.Xml.XmlTextReader(oReq.GetResponse.GetResponseStream)
        With oRdr
            .MoveToContent()
            .MoveToAttribute("code")
            .ReadAttributeValue()
            iErrCode = Int32.Parse(.Value)
            If iErrCode = 0 Then
                sSrvUrl = sServiceUrl
                .ReadToFollowing("value")
                sSecurityToken = .ReadElementContentAsString()
                sErrMsg = ""
            Else
                .ReadToFollowing("error")
                sErrMsg = .ReadElementContentAsString()
                sSecurityToken = ""
            End If
        End With
        Return IIf(iErrCode = 0, True, False)
    End Function

    ''' <summary>Connect to calendar service</summary>
    ''' <returns>True if connection was sucessfull</returns>
    ''' <exception cref="System.Net.WebException">If not already connected to calendar service</exception>
    Public Function Disconnect() As Boolean

        If Len(sSecurityToken) = 0 Then
            Throw New System.Security.SecurityException("Not connected to calendar service")
        Else
            Dim oReq As System.Net.HttpWebRequest = System.Net.HttpWebRequest.Create(sSrvUrl + "?command=disconnect&token=" + sSecurityToken)
            Dim oRdr As New System.Xml.XmlTextReader(oReq.GetResponse.GetResponseStream)
            With oRdr
                .MoveToContent()
                .MoveToAttribute("code")
                .ReadAttributeValue()
                iErrCode = Int32.Parse(.Value)
                If iErrCode = 0 Then
                    .ReadToFollowing("value")
                    If StrComp(.ReadElementContentAsString(), "true", Microsoft.VisualBasic.CompareMethod.Text) = 0 Then
                        sSecurityToken = ""
                    End If
                    sErrMsg = ""
                Else
                    .ReadToFollowing("error")
                    sErrMsg = .ReadElementContentAsString()
                End If
            End With
            Return IIf(iErrCode = 0, True, False)
        End If
    End Function

    ''' <summary>Check whether a given room is available between two dates</summary>
    ''' <param name="sRoom">Room Name</param>
    ''' <param name="dtStart">Start Date</param>
    ''' <param name="dtEnd">End Date</param>
    ''' <returns>True if room is is available between the given dates</returns>
    ''' <exception cref="System.Net.WebException"></exception>
    Public Function IsAvailable(ByVal sRoom As String, ByVal dtStart As Date, ByVal dtEnd As Date) As Boolean
        If Len(sSecurityToken) = 0 Then
            Throw New System.Security.SecurityException("Not connected to calendar service")
        Else
            Dim oReq As System.Net.HttpWebRequest = System.Net.HttpWebRequest.Create(sSrvUrl + "?command=isAvailableRoom&token=" + sSecurityToken + "&room=" + sRoom + "&startdate=" + Format(dtStart, "yyyyMMddHHmmss") + "&enddate=" + Format(dtEnd, "yyyyMMddHHmmss"))
            Dim oRdr As New System.Xml.XmlTextReader(oReq.GetResponse.GetResponseStream)
            Dim bAvailable As Boolean

            With oRdr
                .MoveToContent()
                .MoveToAttribute("code")
                .ReadAttributeValue()
                iErrCode = Int32.Parse(.Value)
                If iErrCode = 0 Then
                    .ReadToFollowing("value")
                    bAvailable = IIf(.ReadElementContentAsString() = "true", True, False)
                    sErrMsg = ""
                Else
                    .ReadToFollowing("error")
                    sErrMsg = .ReadElementContentAsString()
                    bAvailable = False
                End If
            End With
            Return bAvailable
        End If
    End Function

    Private Function GetRooms(ByVal sType As String, ByVal bAvailable As Boolean, ByVal dtStart As Date, ByVal dtEnd As Date) As CalendarRoom()
        If Len(sSecurityToken) = 0 Then
            Throw New System.Security.SecurityException("Not connected to calendar service")
        Else
            Dim oReq As System.Net.HttpWebRequest = System.Net.HttpWebRequest.Create(sSrvUrl + "?command=" + IIf(bAvailable, "getAvailableRooms", "getRooms") + "&token=" + sSecurityToken + IIf(Len(sType) > 0, "&type=" + sType, "") + IIf(bAvailable, "&startdate=" + Format(dtStart, "yyyyMMddHHmmss") + "&enddate=" + Format(dtEnd, "yyyyMMddHHmmss"), ""))
            Dim oRdr As New System.Xml.XmlTextReader(oReq.GetResponse.GetResponseStream)

            With oRdr
                .MoveToContent()
                .MoveToAttribute("code")
                .ReadAttributeValue()
                iErrCode = Int32.Parse(.Value)
                If iErrCode = 0 Then
                    .ReadToFollowing("value")
                    Dim nRooms As Int32 = Int32.Parse(.ReadElementContentAsString())
                    If nRooms > 0 Then
                        Dim aRooms(0 To nRooms - 1) As CalendarRoom
                        Dim oRoom As CalendarRoom
                        For r = 0 To nRooms - 1
                            oRoom = New CalendarRoom
                            .Read()
                            .MoveToAttribute("type")
                            oRoom.type = .Value()
                            .MoveToAttribute("active")
                            oRoom.active = IIf(.Value() = "1", True, False)
                            .Read()
                            If .NodeType = Xml.XmlNodeType.Element Then
                                oRoom.name = .ReadString()
                            Else
                                oRoom.name = ""
                            End If
                            .Read()
                            If .NodeType = Xml.XmlNodeType.Element Then
                                oRoom.comments = .ReadElementContentAsString()
                            Else
                                oRoom.comments = ""
                            End If
                            aRooms(r) = oRoom
                        Next
                        sErrMsg = ""
                        Return aRooms
                    Else
                        sErrMsg = ""
                        Return Nothing
                    End If
                Else
                    .ReadToFollowing("error")
                    sErrMsg = .ReadElementContentAsString()
                    Return Nothing
                End If
            End With
        End If
    End Function

    ''' <summary>Get all available rooms</summary>
    ''' <returns>Array of CalendarRoom objects or Nothing if no rooms where found</returns>
    ''' <remarks>This method returns all active rooms</remarks>
    ''' <exception cref="System.Net.WebException"></exception>
    Public Function GetAvailableRooms(ByVal dtStart As Date, ByVal dtEnd As Date) As CalendarRoom()
        Return GetRooms("", True, dtStart, dtEnd)
    End Function

    ''' <summary>Get all available rooms of a given type</summary>
    ''' <returns>Array of CalendarRoom objects or Nothing if no rooms where found</returns>
    ''' <remarks>This method returns all active rooms of a given type</remarks>
    ''' <exception cref="System.Net.WebException"></exception>
    Public Function GetAvailableRooms(ByVal sType As String, ByVal dtStart As Date, ByVal dtEnd As Date) As CalendarRoom()
        Return GetRooms(sType, True, dtStart, dtEnd)
    End Function

    ''' <summary>Get all rooms</summary>
    ''' <returns>Array of CalendarRoom objects or Nothing if no rooms where found</returns>
    ''' <remarks>This method returns all active and inactive rooms</remarks>
    ''' <exception cref="System.Net.WebException"></exception>
    Public Function GetRooms() As CalendarRoom()
        Return GetRooms("", False, New Date(), New Date())
    End Function

    ''' <summary>Get all rooms of a given type</summary>
    ''' <param name="sType">If it is an empty String then rooms of all types are returned.</param>
    ''' <returns>Array of CalendarRoom objects or Nothing if no rooms where found</returns>
    ''' <remarks>This method returns all active and inactive rooms</remarks>
    ''' <exception cref="System.Net.WebException"></exception>
    Public Function GetRooms(ByVal sType As String) As CalendarRoom()
        Return GetRooms(sType, False, New Date(), New Date())
    End Function

    Private Function ParseResponse(ByRef oRdr As System.Xml.XmlTextReader) As CalendarMeeting()
        Dim oFmt As New System.Globalization.DateTimeFormatInfo()
        With oRdr
            .ReadToFollowing("value")
            Dim sValue As String = .ReadElementContentAsString()
            Dim nMeets As Int32 = Int32.Parse(IIf(sValue = "true" Or Len(sValue) = 0, "1", "0"))
            If nMeets > 0 Then
                Dim aMeets(0 To nMeets - 1) As CalendarMeeting
                Dim oMeet As CalendarMeeting
                Dim oFllw As CalendarAttendant
                For m = 0 To nMeets - 1
                    oMeet = New CalendarMeeting
                    .Read()
                    .MoveToAttribute("type")
                    oMeet.type = .Value()
                    .Read()
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oMeet.id = .ReadString
                    Else
                        oMeet.id = ""
                    End If
                    .Read()
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oMeet.guid = .ReadString
                    Else
                        oMeet.guid = ""
                    End If
                    .Read()
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oMeet.startdate = Date.Parse(.ReadString())
                    End If
                    .Read()
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oMeet.enddate = Date.Parse(.ReadString())
                    End If
                    .Read()
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oMeet.IsPrivate = IIf(.ReadString() = "1", True, False)
                    Else
                        oMeet.IsPrivate = False
                    End If
                    .Read()
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oMeet.title = .ReadString()
                    Else
                        oMeet.title = ""
                    End If
                    .Read()
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oMeet.description = .ReadString()
                    Else
                        oMeet.description = ""
                    End If
                    .Read()
                    .Read()
                    oFllw = New CalendarAttendant
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oFllw.id = .ReadString()
                    Else
                        oFllw.id = ""
                    End If
                    .Read()
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oFllw.guid = .ReadString()
                    Else
                        oFllw.guid = ""
                    End If
                    .Read()
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oFllw.name = .ReadString()
                    Else
                        oFllw.name = ""
                    End If
                    .Read()
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oFllw.surname = .ReadString()
                    Else
                        oFllw.surname = ""
                    End If
                    .Read()
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oFllw.email = .ReadString()
                    Else
                        oFllw.email = ""
                    End If
                    .Read()
                    If .NodeType = Xml.XmlNodeType.Element Then
                        oFllw.timezone = .ReadString()
                    Else
                        oFllw.timezone = "+00:00"
                    End If
                    oMeet.setOrganizer(oFllw)
                    .Read()
                    .Read()
                    .MoveToAttribute("count")
                    Dim nRooms As Int32 = Int32.Parse(.Value())
                    If nRooms > 0 Then
                        Dim aRooms(0 To nRooms - 1) As CalendarRoom
                        Dim oRoom As CalendarRoom
                        For r = 0 To nRooms - 1
                            oRoom = New CalendarRoom
                            .Read()
                            .MoveToAttribute("type")
                            oRoom.type = .Value()
                            .MoveToAttribute("active")
                            oRoom.active = IIf(.Value() = "1", True, False)
                            .Read()
                            If .NodeType = Xml.XmlNodeType.Element Then
                                oRoom.name = .ReadString()
                            Else
                                oRoom.name = ""
                            End If
                            .Read()
                            If .NodeType = Xml.XmlNodeType.Element Then
                                oRoom.comments = .ReadElementContentAsString()
                            Else
                                oRoom.comments = ""
                            End If
                            aRooms(r) = oRoom
                        Next
                        oMeet.rooms = aRooms
                    End If
                    .Read()
                    .Read()
                    .MoveToAttribute("count")
                    Dim nAttns As Int32 = Int32.Parse(.Value())
                    If nAttns > 0 Then
                        Dim aAttns(0 To nAttns - 1) As CalendarAttendant
                        Dim oAttn As CalendarAttendant
                        For a = 0 To nAttns - 1
                            oAttn = New CalendarAttendant
                            .Read()
                            .Read()
                            If .NodeType = Xml.XmlNodeType.Element Then
                                oAttn.id = .ReadString
                            Else
                                oAttn.id = ""
                            End If
                            .Read()
                            If .NodeType = Xml.XmlNodeType.Element Then
                                oAttn.guid = .ReadString
                            Else
                                oAttn.guid = ""
                            End If
                            .Read()
                            If .NodeType = Xml.XmlNodeType.Element Then
                                oAttn.name = .ReadString
                            Else
                                oAttn.name = ""
                            End If
                            .Read()
                            If .NodeType = Xml.XmlNodeType.Element Then
                                oAttn.surname = .ReadString
                            Else
                                oAttn.surname = ""
                            End If
                            .Read()
                            If .NodeType = Xml.XmlNodeType.Element Then
                                oAttn.email = .ReadString
                            Else
                                oAttn.email = ""
                            End If
                            .Read()
                            If .NodeType = Xml.XmlNodeType.Element Then
                                oAttn.timezone = .ReadElementContentAsString()
                            Else
                                oAttn.timezone = ""
                            End If
                            aAttns(a) = oAttn
                        Next
                        oMeet.attendants = aAttns
                    End If
                    .Read()
                    .Read()
                    aMeets(m) = oMeet
                Next
                sErrMsg = ""
                Return aMeets
            Else
                sErrMsg = ""
                Return Nothing
            End If
        End With
    End Function

    Private Function GetMeetings(ByRef dtStart As Date, ByRef dtEnd As Date, ByVal sType As String, ByVal sRoom As String, ByVal sMeeting As String) As CalendarMeeting()
        If Len(sSecurityToken) = 0 Then
            Throw New System.Security.SecurityException("Not connected to calendar service")
        Else
            Dim oReq As System.Net.HttpWebRequest

            If Len(sMeeting) > 0 Then
                oReq = System.Net.HttpWebRequest.Create(sSrvUrl + "?command=getMeeting&token=" + sSecurityToken + "&meeting=" + sMeeting)
            Else
                oReq = System.Net.HttpWebRequest.Create(sSrvUrl + "?command=" + IIf(Len(sRoom) > 0, "getMeetingsForRoom" + "&room=" + sRoom, "getMeetings") + "&token=" + sSecurityToken + "&startdate=" + Format(dtStart, "yyyyMMddHHmmss") + "&enddate=" + Format(dtEnd, "yyyyMMddHHmmss") + IIf(Len(sType) > 0, "&type=" + sType, ""))
            End If

            Dim oRdr As New System.Xml.XmlTextReader(oReq.GetResponse.GetResponseStream())

            With oRdr
                .MoveToContent()
                .MoveToAttribute("code")
                .ReadAttributeValue()
                iErrCode = Int32.Parse(.Value)
                If iErrCode = 0 Then
                    Return ParseResponse(oRdr)
                Else
                    .ReadToFollowing("error")
                    sErrMsg = .ReadElementContentAsString()
                    Return Nothing
                End If ' iErroCode
            End With
        End If
    End Function

    ''' <summary>Get a single meeting</summary>
    ''' <param name="sMeetingId">Meeting unique identifier, either iCalendar Id. or GUID</param>
    ''' <returns>CalendarMeeting instance or Nothing if no meeting with such Id. was found</returns>
    ''' <exception cref="System.Net.WebException"></exception>
    Public Function GetMeeting(ByVal sMeetingId As String) As CalendarMeeting
        If sMeetingId Is Nothing Or Len(sMeetingId) = 0 Then
            Throw New System.NullReferenceException("Meeting Id. is required")
        Else
            Dim aMeets As CalendarMeeting() = GetMeetings(Nothing, Nothing, "", "", sMeetingId)
            If aMeets Is Nothing Then
                Return Nothing
            Else
                Return aMeets(0)
            End If
        End If
    End Function

    ''' <summary>Get list of meetings between two dates</summary>
    ''' <param name="dtStart">Start Date</param>
    ''' <param name="dtEnd">End Date</param>
    ''' <returns></returns>
    ''' <remarks></remarks>
    Public Function GetMeetings(ByVal dtStart As Date, ByVal dtEnd As Date) As CalendarMeeting()
        Return GetMeetings(dtStart, dtEnd, "", "", "")
    End Function

    ''' <summary>Get list of meetings of a given type between two dates</summary>
    ''' <param name="dtStart">Start Date</param>
    ''' <param name="dtEnd">End Date</param>
    ''' <param name="sType">Meeting type</param>
    ''' <exception cref="System.Net.WebException"></exception>
    Public Function GetMeetingsOfType(ByVal dtStart As Date, ByVal dtEnd As Date, ByVal sType As String) As CalendarMeeting()
        Return GetMeetings(dtStart, dtEnd, sType, "", "")
    End Function

    ''' <summary>Get list of meeting using a given room between two dates</summary>
    ''' <param name="dtStart">Start Date</param>
    ''' <param name="dtEnd">End Date</param>
    ''' <param name="sRoom">Room Name</param>
    ''' <exception cref="System.Net.WebException"></exception>
    Public Function GetMeetingsForRoom(ByVal dtStart As Date, ByVal dtEnd As Date, ByVal sRoom As String) As CalendarMeeting()
        Return GetMeetings(dtStart, dtEnd, "", sRoom, "")
    End Function

    Public Function StoreMeeting(ByRef oMeet As CalendarMeeting) As CalendarMeeting
        Dim bAllAttendantsHaveEMail As Boolean
        Dim bAllRoomsAreActive As Boolean
        Dim sReq As String
        Dim UTF8 As New System.Text.UTF8Encoding

        If Len(sSecurityToken) = 0 Then
            Throw New System.Security.SecurityException("Not connected to calendar service")
        Else
            If oMeet.attendants Is Nothing Then
                bAllAttendantsHaveEMail = True
            Else
                bAllAttendantsHaveEMail = True
                For Each a In oMeet.attendants
                    If a.email Is Nothing Then
                        bAllAttendantsHaveEMail = False
                    End If
                Next
            End If
            If Not bAllAttendantsHaveEMail Then
                Throw New System.ArgumentException("One or more attendants do not have a valid e-mail address set")
            Else
                If oMeet.rooms Is Nothing Then
                    bAllRoomsAreActive = True
                Else
                    bAllRoomsAreActive = True
                    For Each r In oMeet.rooms
                        If Not r.active Then
                            bAllRoomsAreActive = False
                        End If
                    Next
                End If
                If Not bAllRoomsAreActive Then
                    Throw New System.ArgumentException("One or more rooms are inactive")
                Else
                    sReq = sSrvUrl + "?command=storeMeeting&token=" + sSecurityToken
                    sReq = sReq + "&meeting=" + oMeet.id
                    sReq = sReq + "&title=" + System.Web.HttpUtility.UrlEncode(oMeet.title, UTF8)
                    sReq = sReq + "&description=" + System.Web.HttpUtility.UrlEncode(oMeet.description, UTF8)
                    sReq = sReq + "&startdate=" + Format(oMeet.startdate, "yyyyMMddHHmmss")
                    sReq = sReq + "&enddate=" + Format(oMeet.enddate, "yyyyMMddHHmmss")
                    sReq = sReq + "&privacy=" + IIf(oMeet.IsPrivate, "1", "0")
                    If Not oMeet.rooms Is Nothing Then
                        sReq = sReq + "&rooms=" + System.Web.HttpUtility.UrlEncode(oMeet.RoomsList(), UTF8)
                    End If
                    If Not oMeet.attendants Is Nothing Then
                        sReq = sReq + "&attendants=" + System.Web.HttpUtility.UrlEncode(oMeet.AttendantsList(), UTF8)
                    End If

                    Dim oReq As System.Net.HttpWebRequest = System.Net.HttpWebRequest.Create(sReq)
                    Dim oRdr As New System.Xml.XmlTextReader(oReq.GetResponse.GetResponseStream)

                    With oRdr
                        .MoveToContent()
                        .MoveToAttribute("code")
                        .ReadAttributeValue()
                        iErrCode = Int32.Parse(.Value)
                        If iErrCode = 0 Then
                            Return ParseResponse(oRdr)(0)
                        Else
                            .ReadToFollowing("error")
                            sErrMsg = .ReadElementContentAsString()
                            Return Nothing
                        End If ' iErroCode
                    End With
                End If
            End If
        End If
    End Function

    ''' <summary>Delete a meeting</summary>
    ''' <param name="sMeetingId">iCalendar identifier of meeting</param>
    ''' <exception cref="System.Net.WebException"></exception>
    Public Function DeleteMeeting(ByVal sMeetingId As String) As Boolean
        If Len(sSecurityToken) = 0 Then
            Throw New System.Security.SecurityException("Not connected to calendar service")
        Else
            Dim sReq As String
            sReq = sSrvUrl + "?command=deleteMeeting&token=" + sSecurityToken
            sReq = sReq + "&meeting=" + sMeetingId

            Dim oReq As System.Net.HttpWebRequest = System.Net.HttpWebRequest.Create(sReq)
            Dim oRdr As New System.Xml.XmlTextReader(oReq.GetResponse.GetResponseStream)

            With oRdr
                .MoveToContent()
                .MoveToAttribute("code")
                .ReadAttributeValue()
                iErrCode = Int32.Parse(.Value)
                If iErrCode = 0 Then
                    Return True
                Else
                    .ReadToFollowing("error")
                    sErrMsg = .ReadElementContentAsString()
                    Return False
                End If ' iErroCode
            End With
        End If
    End Function

    ''' <summary>Print an array of rooms as XML</summary>
    ''' <param name="aRooms">Array of CalendarRoom</param>
    Public Function ToXML(ByRef aRooms As CalendarRoom()) As String
        Dim sXml As String
        If aRooms Is Nothing Then
            sXml = "<rooms count=" + Chr(34) + "0" + Chr(34) + "></rooms>"
        Else
            sXml = "<rooms count=" + Chr(34) + CStr(aRooms.Count) + Chr(34) + ">"
            For Each r In aRooms
                sXml = sXml + r.ToXML
            Next
            sXml = sXml + "</rooms>"
        End If
        Return sXml
    End Function

    ''' <summary>Print an array of meetings as XML</summary>
    ''' <param name="aMeetings">Array of CalendarMeeting</param>
    Public Function ToXML(ByRef aMeetings As CalendarMeeting()) As String
        Dim sXml As String
        If aMeetings Is Nothing Then
            sXml = "<meetings count=" + Chr(34) + "0" + Chr(34) + "></meetings>"
        Else
            sXml = "<meetings count=" + Chr(34) + CStr(aMeetings.Count) + Chr(34) + ">"
            For Each m In aMeetings
                sXml = sXml + m.ToXML
            Next
            sXml = sXml + "</meetings>"
        End If
        Return sXml
    End Function

End Class

