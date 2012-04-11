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

Public Class CalendarRoom
    Private bActive As Boolean = False
    Private sType As String = ""
    Private sName As String = ""
    Private sComments As String = ""

    ''' <summary>Is resource allocatable for a meeting</summary>
    ''' <remarks>Only active resource can be added as resources to a meeting</remarks>
    Public Property active() As Boolean
        Get
            Return bActive
        End Get
        Set(ByVal bValue As Boolean)
            bActive = bValue
        End Set
    End Property

    ''' <summary>Resource type</summary>
    ''' <exception cref="System.ArgumentOutOfRangeException">If type is longer then 16 characters</exception>
    Public Property type() As String
        Get
            Return sType
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 16 Then
                Throw New System.ArgumentOutOfRangeException("Room type may not exceed 16 characters")
            End If
            sType = sValue
        End Set
    End Property

    ''' <exception cref="System.ArgumentOutOfRangeException">If name is longer then 50 characters</exception>
    Public Property name() As String
        Get
            Return sName
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 50 Then
                Throw New System.ArgumentOutOfRangeException("Room name may not exceed 50 characters")
            End If
            sName = sValue
        End Set
    End Property

    ''' <exception cref="System.ArgumentOutOfRangeException">If comments is longer then 254 characters</exception>
    Public Property comments() As String
        Get
            Return sComments
        End Get
        Set(ByVal sValue As String)
            If Len(sValue) > 254 Then
                Throw New System.ArgumentOutOfRangeException("Room comments may not exceed 254 characters")
            End If
            sComments = sValue
        End Set
    End Property

    Public Function ToXML() As String
        Return "<room type=" + Chr(34) + Me.type + Chr(34) + "><name>" + Me.name + "</name><active>" + IIf(Me.active, "true", "false") + "</active><comments>" + Me.comments + "</comments></room>"
    End Function
End Class
