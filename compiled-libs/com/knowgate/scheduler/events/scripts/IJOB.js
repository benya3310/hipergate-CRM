
import com.knowgate.jdc.JDCConnection;
import com.knowgate.hipermail.SendMail;

Integer ReturnValue;
Integer ErrorCode;
String  ErrorMessage;

SendMail.send(EnvironmentProperties, "body", "subject",
			  "info@eoi.es", "EOI Bot", "info@eoi.es"
			  new String[]{"sergiom@knowgate.com"});

ErrorMessage = "";
ErrorCode = new Integer(0);
ReturnValue = new Integer(0);
