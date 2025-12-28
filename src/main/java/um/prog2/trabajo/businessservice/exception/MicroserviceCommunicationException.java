package um.prog2.trabajo.businessservice.exception;

public class MicroserviceCommunicationException extends RuntimeException {
    public MicroserviceCommunicationException(String mensaje) {
        super(mensaje);
    }
    
    public MicroserviceCommunicationException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
