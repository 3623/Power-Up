import socket

class UDPRioServer:

    def __init__(self, port1=36231, port2=36232, port3=36233):
        self.server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.port1 = port1
        self.rio_ip = socket.gethostbyname("roboRIO-3623-FRC.local")

    def refindRio(self):
        self.rio_ip = socket.gethostbyname("roboRIO-3623-FRC.local")

    def switchPort(self, new_port):
        self.udp_port = new_port

    def send1(self, message):
        sock.sendto(message, (self.rio_ip, self.port1))

    def send2(self, message):
        sock.sendto(message, (self.rio_ip, self.port2))

    def send3(self, message):
        sock.sendto(message, (self.rio_ip, self.port3))

