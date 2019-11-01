import java.io.*;  // for ByteArrayInputStream
import java.net.*; // for DatagramPacket

public class TCPRequestDecoderBin implements TCPRequestDecoder, TCPRequestBinConst {

  private String encoding;  // Character encoding

  public TCPRequestDecoderBin() {
    encoding = DEFAULT_ENCODING;
  }

  public TCPRequestDecoderBin(String encoding) {
    this.encoding = encoding;
  }

  public TCPRequest decode(InputStream wire) throws IOException {
    DataInputStream src = new DataInputStream(wire);
    byte tml            = src.readByte();
    byte requestID      = src.readByte();
    byte opCode         = src.readByte();
    byte numOperands    = src.readByte();
    short op1           = src.readShort();
    short op2           = src.readShort();

    return new TCPRequest(tml, requestID, opCode, numOperands, op1, op2);
  }

  public TCPRequest decode(DatagramPacket p) throws IOException {
    ByteArrayInputStream payload =
      new ByteArrayInputStream(p.getData(), p.getOffset(), p.getLength());
    return decode(payload);
  }

  public TCPResponse decodeRes(InputStream wire) throws IOException {
    DataInputStream src = new DataInputStream(wire);
    byte tml            = src.readByte();
    byte requestID      = src.readByte();
    byte errorCode      = src.readByte();
    int result          = src.readInt();

    return new TCPResponse(tml, requestID, errorCode, result);
  }

  public TCPResponse decodeRes(DatagramPacket p) throws IOException {
    ByteArrayInputStream payload =
      new ByteArrayInputStream(p.getData(), p.getOffset(), p.getLength());
    return decodeRes(payload);
  }
}
