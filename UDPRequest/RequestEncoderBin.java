import java.io.*;  // for ByteArrayOutputStream and DataOutputStream

public class RequestEncoderBin implements RequestEncoder, RequestBinConst {

  private String encoding;  // Character encoding

  public RequestEncoderBin() {
    encoding = DEFAULT_ENCODING;
  }

  public RequestEncoderBin(String encoding) {
    this.encoding = encoding;
  }

  public byte[] encode(Request request) throws Exception {

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(buf);
    out.writeByte(request.tml);
    out.writeByte(request.requestID);
    out.writeByte(request.opCode);
    out.writeByte(request.numOperands);
    out.writeShort(request.op1);
    out.writeShort(request.op2);

    out.flush();
    return buf.toByteArray();
  }

  public byte[] encode(Response response) throws Exception {

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(buf);
    out.writeByte(response.tml);
    out.writeByte(response.requestID);
    out.writeByte(response.errorCode);
    out.writeInt(response.result);

    out.flush();
    return buf.toByteArray();
  }
}
