public class TCPResponse {
  public byte tml;
  public byte requestID;
  public byte errorCode = 0;
  public int result;

  public TCPResponse(byte tml, byte requestID, byte errorCode, int result) {
    this.tml = tml;
    this.requestID = requestID;
    this.errorCode = errorCode;
    this.result = result;
  }

  public String toString() {
    final String EOLN = java.lang.System.getProperty("line.separator");
    String value = "TML = " + tml + EOLN +
                   "Request ID = " + requestID + EOLN +
                   "Error Code = " + errorCode + EOLN +
                   "Result = " + result + EOLN;

    return value;
  }
}
