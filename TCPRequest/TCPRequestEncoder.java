public interface TCPRequestEncoder {
  byte[] encode(TCPRequest request) throws Exception;
  byte[] encode(TCPResponse response) throws Exception;
}
