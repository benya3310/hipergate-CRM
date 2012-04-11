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

Public Class CalendarMeeting
    Private sId As String
    Private sGu As String
    Private sType As String
    Private sTitle As String
    Private sDescription As String
    Private dtStart As Date
    Private dtEnd As Date
    Private bPrivate As Boolean
    Private oOrganizer As CalendarAttendant
    Private oAttendants As CalendarAttendant()
    Private oRooms As CalendarRoom()

    ''' <summary>iCalendar Unique Identifier of Meeting</summary>
    ''' <remarks>The iCalendar Identifier may not exceed 254 characters</remarks>
    ''' <exception cref="System.ArgumentOutOfRangeException">If id is longer than 254 characters</exception>
    Public Property id() As String
        Get
            Dim g As System.Guid = System.Guid.NewGuid()
            sId = IIf(sId Is Nothing, g.ToString() + "@hipergate.org", IIf(Len(sId) = 0, g.ToString() + "@hipergate.org", sId))
            Return sId
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 254 Then
                Throw New System.ArgumentOutOfRangeException("Meeting id may not exceed 254 characters")
            End If
            sId = sValue
        End Set
    End Property

    ''' <summary>Global Unique Identifier of Meeting</summary>
    ''' <remarks>The Global Unique Identifier may not exceed 32 characters</remarks>
    ''' <exception cref="System.ArgumentOutOfRangeException">If guid is longer than 32 characters</exception>
    Public Property guid() As String
        Get
            Return IIf(sGu Is Nothing, "", sGu)
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 32 Then
                Throw New System.ArgumentOutOfRangeException("Meeting guid may not exceed 32 characters")
            End If
            sGu = sValue
        End Set
    End Property

    ''' <summary>Meeting type</summary>
    ''' <value>One of: { meeting,call,followup,breakfast,lunch,,course,demo,workshop,congress,tradeshow }</value>
    ''' <remarks>The meeting type may not exceed 16 characters</remarks>
    ''' <exception cref="System.ArgumentOutOfRangeException">If type is longer than 16 characters</exception>
    Public Property type() As String
        Get
            Return IIf(sType Is Nothing, "", sType)
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 16 Then
                Throw New System.ArgumentOutOfRangeException("Meeting type may not exceed 16 characters")
            End If
            sType = sValue
        End Set
    End Property

    ''' <summary>Meeting Title</summary>
    ''' <remarks>The meeting title may not exceed 100 characters</remarks>
    ''' <exception cref="System.ArgumentOutOfRangeException">If title is longer than 100 characters</exception>
    Public Property title() As String
        Get
            Return IIf(sTitle Is Nothing, "", sTitle)
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 100 Then
                Throw New System.ArgumentOutOfRangeException("Meeting title may not exceed 100 characters")
            End If
            sTitle = sValue
        End Set
    End Property

    ''' <summary>Meeting Description</summary>
    ''' <remarks>The meeting description may not exceed 1000 characters</remarks>
    ''' <exception cref="System.ArgumentOutOfRangeException">If description is longer than 1000 characters</exception>
    Public Property description() As String
        Get
            Return IIf(sDescription Is Nothing, "", sDescription)
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 100 Then
                Throw New System.ArgumentOutOfRangeException("Meeting description may not exceed 1000 characters")
            End If
            sDescription = sValue
        End Set
    End Property

    Public Property startdate() As Date
        Get
            Return dtStart
        End Get
        Set(ByVal dtValue As Date)
            dtStart = dtValue
        End Set
    End Property

    Public Property enddate() As Date
        Get
            Return dtEnd
        End Get
        Set(ByVal dtValue As Date)
            dtEnd = dtValue
        End Set
    End Property

    ''' <summary>Person who organized the meeting</summary>
    ''' <returns></returns>
    ''' <remarks>This property is read-only because when writting new meetings the organizer is always the user connected to the service</remarks>
    Public ReadOnly Property organizer() As CalendarAttendant
        Get
            Return oOrganizer
        End Get
    End Property

    Friend Sub setOrganizer(ByRef oOrg As CalendarAttendant)
        oOrganizer = oOrg
    End Sub

    ''' <summary>Array of people attendint to the meeting</summary>
    ''' <remarks>The organizer is always among the array of attendants</remarks>
    Public Property attendants() As CalendarAttendant()
        Get
            Return oAttendants
        End Get
        Set(ByVal oValue As CalendarAttendant())
            oAttendants = oValue
        End Set
    End Property

    ''' <summary>Array of resources used by the meeting</summary>
    Public Property rooms() As CalendarRoom()
        Get
            Return oRooms
        End Get
        Set(ByVal oValue As CalendarRoom())
            oRooms = oValue
        End Set
    End Property

    ''' <summary>Get whether this meeting is only visible to its attendants</summary>
    Public Property IsPrivate() As Boolean
        Get
            Return bPrivate
        End Get
        Set(ByVal bValue As Boolean)
            bPrivate = bValue
        End Set
    End Property

    ''' <summary>Add resource to meeting</summary>
    ''' <remarks>The added resource must already exist at server side</remarks>
    Public Sub AddRoom(ByRef oRoom As CalendarRoom)
        If Not oRoom.active Then
            Throw New System.ArgumentException("Only active rooms can be added to a meeting")
        Else
            If oRooms Is Nothing Then
                ReDim oRooms(0 To 0)
                oRooms(0) = oRoom
            Else
                ReDim Preserve oRooms(0 To UBound(oRooms) + 1)
                oRooms(UBound(oRooms)) = oRoom
            End If
        End If
    End Sub

    ''' <summary>Add resource to meeting</summary>
    ''' <param name="sRoomName">Resource Name</param>
    ''' <param name="sType">Resource Type</param>
    ''' <param name="sComments">Resource Comments</param>
    ''' <remarks>The added resource must already exist at server side</remarks>
    Public Sub AddRoom(ByVal sRoomName As String, Optional ByVal sType As String = "", Optional ByVal sComments As String = "")
        Dim r As New CalendarRoom
        r.active = True
        r.name = sRoomName
        If Len(sType) > 0 Then
            r.type = sType
        End If
        If Len(sComments) > 0 Then
            r.comments = sComments
        End If
        AddRoom(r)
    End Sub

    ''' <summary>Add attendant to meeting</summary>
    ''' <remarks>The added attendant must already exist at server side</remarks>
    Public Sub AddAttendant(ByRef oAttn As CalendarAttendant)
        If oAttendants Is Nothing Then
            ReDim oAttendants(0 To 0)
            oAttendants(0) = oAttn
        Else
            ReDim Preserve oAttendants(0 To UBound(oAttendants) + 1)
            oAttendants(UBound(oAttendants)) = oAttn
        End If
    End Sub

    ''' <summary>Add attendant to meeting</summary>
    ''' <param name="sEmail">e-mail</param>
    ''' <param name="sName">Name</param>
    ''' <param name="sSurName">Surname</param>
    ''' <param name="sId">iCalendar Idenfier of the attendant</param>
    ''' <param name="sGuid">Global Unique Identifier of the attendant</param>
    ''' <param name="sTimezone">Time zone with format [+|-]HH:MM</param>
    ''' <remarks></remarks>
    Public Sub AddAttendant(ByVal sEmail As String, Optional ByVal sName As String = "", Optional ByVal sSurName As String = "", Optional ByVal sId As String = "", Optional ByVal sGuid As String = "", Optional ByVal sTimezone As String = "+00:00")
        Dim a As New CalendarAttendant
        a.email = sEmail
        If Len(sName) > 0 Then
            a.name = sName
        End If
        If Len(sSurName) > 0 Then
            a.surname = sSurName
        End If
        If Len(sId) > 0 Then
            a.id = sId
        End If
        If Len(sGuid) > 0 Then
            a.guid = sGuid
        End If
        If Len(sTimezone) > 0 Then
            a.timezone = sTimezone
        End If
        AddAttendant(a)
    End Sub

    ''' <summary>Check whether this meeting uses a given resource</summary>
    ''' <remarks>This is a client-side only method, no connection to the server is performed</remarks>
    Public Function UsesRoom(ByVal sRoomName As String) As Boolean
        If Not oRooms Is Nothing Then
            For Each r In oRooms
                If StrComp(r.name, sRoomName, Microsoft.VisualBasic.CompareMethod.Text) = 0 Then
                    Return True
                End If
            Next
        End If
        Return False
    End Function

    ''' <summary>Check whether a given attendant is scheduled for this meeting</summary>
    ''' <remarks>This is a client-side only method, no connection to the server is performed</remarks>
    Public Function HasAttendant(ByVal sAttendant As String) As Boolean
        If Not oAttendants Is Nothing Then
            For Each a In oAttendants
                If StrComp(a.email, sAttendant, Microsoft.VisualBasic.CompareMethod.Text) = 0 Or _
                   StrComp(a.id, sAttendant, Microsoft.VisualBasic.CompareMethod.Text) = 0 Or _
                   StrComp(a.guid, sAttendant, Microsoft.VisualBasic.CompareMethod.Text) = 0 Or _
                   StrComp(a.name + " " + a.surname, sAttendant, Microsoft.VisualBasic.CompareMethod.Text) = 0 Then
                    Return True
                End If
            Next
        End If
        Return False
    End Function

    ''' <summary>Get resources used by this meeting as 
    ''' </summary>
    ''' <returns></returns>
    ''' <remarks></remarks>
    Public Function RoomsList() As String
        Dim sLst As String = ""
        If Not oRooms Is Nothing Then
            For Each r In oRooms
                sLst = sLst + IIf(Len(sLst) = 0, "", ",") + r.name
            Next
        End If
        Return sLst
    End Function

    Public Function AttendantsList() As String
        Dim sLst As String = ""
        If Not oAttendants Is Nothing Then
            For Each a In oAttendants
                sLst = sLst + IIf(Len(sLst) = 0, "", ",") + a.email
            Next
        End If
        Return sLst
    End Function

    Public Function ToXML() As String
        Dim sXml As String
        sXml = "<meeting type=" + Chr(34) + Me.type + Chr(34) + "><id>" + Me.id + "</id><gu>" + Me.guid + "</gu><startdate>" + Replace(Format(Me.startdate, "yyyy-MM-dd HH:mm:ss"), " ", "T") + "</startdate><enddate>" + Replace(Format(Me.enddate, "yyyy-MM-dd HH:mm:ss"), " ", "T") + "</enddate><privacy>" + IIf(Me.IsPrivate, "1", "0") + "</privacy><title>" + Me.title + "</title><description>" + Me.description + "</description>"
        If Not oOrganizer Is Nothing Then
            sXml = sXml + "<organizer>" + oOrganizer.toXML + "</organizer>"
        Else
            sXml = sXml + "<organizer></organizer>"
        End If
        If Not oRooms Is Nothing Then
            sXml = sXml + "<rooms count=" + Chr(34) + CStr(oRooms.Count) + Chr(34) + ">"
            For Each r In oRooms
                sXml = sXml + r.toXML()
            Next
            sXml = sXml + "</rooms>"
        Else
            sXml = sXml + "<rooms count=" + Chr(34) + "0" + Chr(34) + "></rooms>"
        End If
        If Not oAttendants Is Nothing Then
            sXml = sXml + "<attendants count=" + Chr(34) + CStr(oAttendants.Count) + Chr(34) + ">"
            For Each a In oAttendants
                sXml = sXml + a.toXML()
            Next
            sXml = sXml + "</attendants>"
        Else
            sXml = sXml + "<attendants count=" + Chr(34) + "0" + Chr(34) + "></attendants>"
        End If
        sXml = sXml + "</meeting>"
        Return sXml
    End Function

End Class
