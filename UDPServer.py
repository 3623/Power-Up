import socket

class UDPServer:

    def __init__(self, ip, port=3623):
        self.server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.udp_ip = ip
        self.udp_port = port

    def switchIP(self, new_ip):
        self.udp_ip = new_ip

    def switchPort(self, new_port):
        self.udp_port = new_port

    def send(self, message):
        sock.sendto(message, (self.udp_ip, self.udp_port))

