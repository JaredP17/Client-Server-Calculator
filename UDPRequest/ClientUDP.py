import sys, socket, time, struct

DEFAULT_ENCODING = "ISO-8859-1"
GROUP_ID = 7
quit = False

tml = 8
id = 0
op_code = 0
num_operands = 0
op_1 = 0
op_2 = 0

class Request(object):
    def __init__(self, tml, request_id, op_code, num_operands, op_1, op_2):
        self.tml = tml
        self.request_id = request_id
        self.op_code = op_code
        self.num_operands = num_operands
        self.op_1 = op_1
        self.op_2 = op_2

    def description(self):
        op = ""
        if self.op_code == 0:
            op = "Addition (+)"
        elif self.op_code == 1:
            op = "Subtraction (-)"
        elif self.op_code == 2:
            op = "Multiplication (*)"
        elif self.op_code == 3:
            op = "Division (/)"
        elif self.op_code == 4:
            op = "Shift Right (>>)"
        elif self.op_code == 5:
            op = "Shift Left (<<)"
        elif self.op_code == 6:
            op = "One-complement (NOT)(~)"

        value = """
TML = %d
Request ID = %d
Op Code = %d
Number Operands = %d
Operand 1 = %d
Operand 2 = %s
Operation: = %s
""" %(tml, id, op_code, num_operands, op_1, ("N/A" if num_operands == 1 else str(op_2)), op)
        return value

def usage():
    sys.stdout = sys.stderr
    print "Error: Insufficient command line arguments."
    print "Parameter(s): <Destination> <Port>"
    sys.exit(2)

if len(sys.argv) < 2:
    usage()

dest_address = socket.gethostbyname(sys.argv[1])
dest_port = int(sys.argv[2]) + GROUP_ID
print "Running Calculator-Client."
print "Destination Address:", dest_address
print "Destination Port:", dest_port

while not quit:
    print """
*** Request New Calculation ***
0: Addition (+)
1: Subtraction (-)
2: Multiplication (*)
3: Division (/)
4: Shift Right (>>)
5: Shift Left (<<)
6: One-Complement (NOT)(~)
"""
    op_code = int(input("Enter Operation Code: "))
    if op_code >= 0 and op_code <= 6:
        if op_code == 6:
            num_operands = 1
        else:
            num_operands = 2
    else:
        print "*** Error: Please enter a valid operation code. ***"
        continue

    if num_operands == 1:
        op_1 = int(input("Enter Operand: "))
        op_2 = 0
    elif num_operands == 2:
        op_1 = int(input("Enter Operand 1: "))
        op_2 = int(input("Enter Operand 2: "))

    request = Request(tml, id, op_code, num_operands, op_1, op_2)
    print request.description()

    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    coded_request = bytearray([tml, id, op_code, num_operands, 0, op_1, 0, op_2])
    print "Message sent in bytes:", list(map(hex, list(coded_request)))
    sock.sendto(coded_request, (dest_address, dest_port))
    start = time.time()

    # Get response from server
    print "\nWaiting for response..."
    response = bytearray(7)
    data, addr = sock.recvfrom_into(response)
    stop = time.time()
    print "Message received in bytes:", list(map(hex, list(response)))
    test = list(map(hex, list(response)))
    print int(str(test[5][2:] + test[6][2:]), 16, True)

    # Print total time elapsed
    total_time = stop - start
    print "\nTime elapsed: %f %s" % ((total_time * 1000.0), "ms")

    id += 1
sock.close()
