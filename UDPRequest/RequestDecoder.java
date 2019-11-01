import java.io.*;   // for InputStream and IOException
import java.net.*;  // for DatagramPacket

public interface RequestDecoder {
  Request decode(InputStream source) throws IOException;
  Request decode(DatagramPacket packet) throws IOException;
  Response decodeRes(InputStream source) throws IOException;
  Response decodeRes(DatagramPacket packet) throws IOException;
}
