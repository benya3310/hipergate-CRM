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

Imports System.Text.RegularExpressions

Public Class CalendarAttendant
    Private sId As String
    Private sGu As String
    Private sName As String
    Private sSurname As String
    Private sEmail As String
    Private sTimezone As String

    ''' <summary>Attendant iCalendar Id.</summary>
    ''' <remarks>The attendant id may not exceed 50 characters</remarks>
    ''' <exception cref="System.ArgumentOutOfRangeException">If id is longer than 50 characters</exception>
    Public Property id() As String
        Get
            Return sId
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 50 Then
                Throw New System.ArgumentOutOfRangeException("Attendant id may not exceed 50 characters")
            End If
            sId = sValue
        End Set
    End Property

    ''' <summary>Attendant Global Unique Identifier</summary>
    ''' <remarks>The attendant GUID may not exceed 32 characters</remarks>
    ''' <exception cref="System.ArgumentOutOfRangeException">If guid is longer than 32 characters</exception>
    Public Property guid() As String
        Get
            Return sGu
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 32 Then
                Throw New System.ArgumentOutOfRangeException("Attendant guid may not exceed 32 characters")
            End If
            sGu = sValue
        End Set
    End Property

    ''' <summary>Attendant First Name</summary>
    ''' <remarks>The attendant name may not exceed 100 characters</remarks>
    ''' <exception cref="System.ArgumentOutOfRangeException">If name is longer than 100 characters</exception>
    Public Property name() As String
        Get
            Return sName
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 100 Then
                Throw New System.ArgumentOutOfRangeException("Attendant name may not exceed 100 characters")
            End If
            sName = sValue
        End Set
    End Property

    ''' <summary>Attendant Surname</summary>
    ''' <remarks>The attendant surname may not exceed 100 characters</remarks>
    ''' <exception cref="System.ArgumentOutOfRangeException">If surname is longer than 100 characters</exception>
    Public Property surname() As String
        Get
            Return sSurname
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 100 Then
                Throw New System.ArgumentOutOfRangeException("Attendant surname may not exceed 100 characters")
            End If
            sSurname = sValue
        End Set
    End Property

    ''' <summary>Attendant e-mail address</summary>
    ''' <remarks>The attendant e-mail may not exceed 100 characters</remarks>
    ''' <exception cref="System.ArgumentException">If email has an invalid syntax</exception>
    ''' <exception cref="System.ArgumentOutOfRangeException">If email is longer than 100 characters</exception>
    Public Property email() As String
        Get
            Return sEmail
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 100 Then
                Throw New System.ArgumentOutOfRangeException("Attendant email may not exceed 100 characters")
            End If
            If Regex.IsMatch(sValue, "[\w\x2E_-]+@[\w\x2E_-]+\x2E\D{2,4}") Then
                sEmail = sValue
            Else
                Throw New System.ArgumentException("Attendant email has an invalid syntax")
            End If
        End Set
    End Property

    ''' <summary>Attendant timezone</summary>
    ''' <value>[+|-]hh:mm</value>
    ''' <exception cref="System.ArgumentException">If timezone has an invalid syntax</exception>
    Public Property timezone() As String
        Get
            Return sTimezone
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) = 0 Or Regex.IsMatch(sValue, "[\x2B\x2D][01]\d:[012345]0") Then
                sTimezone = sValue
            Else
                Throw New System.ArgumentOutOfRangeException("Attendant timezone has an invalid syntax")
            End If
        End Set
    End Property

    Public Function toXML() As String
        Return "<attendant><id>" + Me.id + "</id><gu>" + Me.guid + "</gu><email>" + Me.email + "</email><name>" + Me.name + "</name><surname>" + Me.surname + "</surname><timezone>" + Me.timezone + "</timezone></attendant>"
    End Function

End Class
