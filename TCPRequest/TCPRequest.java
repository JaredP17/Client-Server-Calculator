public class TCPRequest {

    public byte tml;            // Total Message Length
    public byte requestID;      // TCPRequest ID
    public byte opCode;         // Op Code
    public byte numOperands;    // Number of Operands
    public short op1;           // Operand 1
    public short op2;           // Operand 2

  public TCPRequest(byte tml, byte requestID, byte opCode,
    byte numOperands, short op1, short op2)  {
      this.tml            = tml;
      this.requestID      = requestID;
      this.opCode         = opCode;
      this.numOperands    = numOperands;
      this.op1            = op1;
      this.op2            = op2;
  }

  public String toString() {
    final String EOLN = java.lang.System.getProperty("line.separator");
    String value = "TML = " + tml + EOLN +
                   "Request ID = " + requestID + EOLN +
                   "Op Code  = " + opCode + EOLN +
                   "Number Operands = " + numOperands + EOLN +
                   "Operand 1 = " + op1 + EOLN +
                   "Operand 2 = " + ((numOperands == 1) ? "N/A" : op2) + EOLN +
                   "\nOperation: ";

    switch (opCode) {
      case 0:
        value += "Addition (+)";
        break;
      case 1:
        value += "Subtraction (-)";
        break;
      case 2:
        value += "Multiplication (*)";
        break;
      case 3:
        value += "Division (/)";
        break;
      case 4:
        value += "Shift Right (>>)";
        break;
      case 5:
        value += "Shift Left (<<)";
        break;
      case 6:
        value += "One-complement (NOT)(~)";
        break;
      default: // Invalid Op Code handled in Client-Server.
               // No need to print invalid code if message isn't sent
    }

    return value;
  }
}
