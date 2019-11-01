public interface RequestEncoder {
  byte[] encode(Request request) throws Exception;
  byte[] encode(Response response) throws Exception;
}
