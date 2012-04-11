Module Module1

    Sub Main()

        ' Crear un objeto cliente de calendario
        Dim c As New Hipergate.CalendarClient

        ' Conectarse al servicio
        c.Connect("http://localhost/hipergate/servlet/HttpCalendarServlet", "administrator@hipergate-test.com", "TEST")

        ' Comprobar si el recurso de nombre NEWTON está disponible ahora mismo
        Dim a As Boolean = c.IsAvailable("NEWTON", DateValue(Now), DateValue(Now))

        ' Obtener un array con todos los recursos
        Dim r() As Hipergate.CalendarRoom = c.GetRooms()

        ' Obtener un array con todos los recursos diponibles ahora mismo
        Dim d() As Hipergate.CalendarRoom = c.GetAvailableRooms(DateValue(Now), DateValue(Now))

        ' Obtener un listado de todas las actividades entre el 1/1/2000 y el 31/12/2020
        Dim m() As Hipergate.CalendarMeeting = c.GetMeetings(New Date(2000, 1, 1, 0, 0, 0), New Date(2020, 12, 31, 23, 59, 59))

        ' Graba una nueva actividad de 2h e duración en el Aula Newton con 1 asistente
        Dim e As New Hipergate.CalendarMeeting
        Dim f As Hipergate.CalendarMeeting
        e.title = "Titulo de la actividad hasta 100 caracteres"
        e.description = "Descripcion larga de la actividad hasta 1000 caracteres"
        e.startdate = Now ' Fecha Inicio
        e.enddate = DateAdd(DateInterval.Hour, 2, e.startdate)
        e.AddRoom("NEWTON") ' Llamar a AddRoom una vez por cada recurso
        e.AddAttendant("administrator@hipergate-test.com")
        f = c.StoreMeeting(e)
        ' Tras grabar la actividad algunos valores adicionales pueden volver en el objeto retornado por el método StoreMeeting
        ' En particular, es relevante la propiedad id que es la que identifica a la actividad unívocamente para futuros accesos

        ' Recuperar la actividad anterior
        Dim g As Hipergate.CalendarMeeting = c.GetMeeting(f.id)
        ' Adelantar 10 minutos la fecha de inicio y regrabar
        g.startdate = DateAdd(DateInterval.Minute, 10, g.startdate)
        c.StoreMeeting(g)

        ' Cerrar la sesión
        c.Disconnect()

    End Sub

End Module
