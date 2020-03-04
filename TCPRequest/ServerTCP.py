import sys, socket, time, struct
import numpy as np

DEFAULT_ENCODING = 'iso-8859-1'
GROUP_ID = 7

def usage():
    sys.stdout = sys.stderr
    print "Error: Insufficient command line arguments."
    print "Parameter(s): <Port>"
    sys.exit(2)

def main():
    if len(sys.argv) < 2:
        usage()

    port = int(sys.argv[1]) + GROUP_ID
    print "Calculator-Server (TCP): Online"
    print "Port: %d" % port

    # Create a TCP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # Enable reuse address/port
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    # Bind the socket to the port
    sock.bind(('', port))
    sock.listen(5)

    while True:
        print "\nWaiting for request...\n"
        #accept connections from outside
        (conn, addr) = sock.accept()
        data = conn.recv(8) # Expected size of TCP Request
        print "Handling client at:", addr[0], "on port", addr[1], "\n"
        packet_size = len(data)
        data = data.encode('hex')

        request = Request()
        request.decode(data)
        request.display()

        tml = request.tml
        id = request.request_id
        err = 0 #Error code
        result = -1

        if tml != packet_size:
            err = 127

        #Perform Calculation
        code = request.op_code #op code
        op1 = request.op_1
        op2 = request.op_2

        if code == 0:
            result = op1 + op2
        elif code == 1:
            result = op1 - op2
        elif code == 2:
            result = op1 * op2
        elif code == 3:
            result = op1 / op2
        elif code == 4:
            result = op1 >> op2
        elif code == 5:
            result = op1 << op2
        elif code == 6:
            result = ~op1
        else:
            err = 127

        print "\nResult:", result
        print "Sending response."

        #Send response
        tml = 7;
        coded_response = struct.pack('!bbbl', tml, id, err, result) # Pack response into specific byte format
        conn.send(coded_response)

class Request(object):
    def __init__(self, tml=0, request_id=0, op_code=0, num_operands=0, op_1=0, op_2=0):
        self.tml = tml # Total message length
        self.request_id = request_id
        self.op_code = op_code
        self.num_operands = num_operands
        self.op_1 = op_1
        self.op_2 = op_2

    def decode(self, data):
        self.tml = int(data[0:2], 16) # 1 byte
        self.request_id = int(data[2:4], 16) # 1 byte
        self.op_code = int(data[4:6], 16) # 1 byte
        self.num_operands = int(data[6:8], 16) # 1 byte
        # np.int16 handles negative operands.
        # Otherwise -1 would be seen as 65535
        self.op_1 = np.int16(int(data[8:12], 16)) # 2 bytes
        self.op_2 = np.int16(int(data[12:16], 16)) # 2 bytes

    def display(self):
        print "TML =", self.tml
        print "Request ID =", self.request_id
        print "Op Code =", self.op_code
        print "Number Operands =", self.num_operands
        print "Operand 1 =", self.op_1
        print "Operand 2 =", ("N/A" if self.num_operands == 1 else self.op_2)
        print "Operation: %s" % ("Addition (+)" if self.op_code == 0
                            else "Subtraction (-)" if self.op_code == 1
                            else "Multiplication (*)" if self.op_code == 2
                            else "Division (/)" if self.op_code == 3
                            else "Shift (>>)" if self.op_code == 4
                            else "Shift (<<)" if self.op_code == 5
                            else "One-complement (NOT)(~)" if self.op_code == 6
                            else "Invalid.")
main()
