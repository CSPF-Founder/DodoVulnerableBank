__author__ = 'CSPF'

import os

from OpenSSL import crypto
import socket, ssl
from utils import get_home_dir


class SSLCertifiacate:

    def __init__(self,host=None):
        self.host = host
        ssl_dir = os.path.join(get_home_dir(), "ssl")
        self.key_path = os.path.join(ssl_dir, "dodo.key")
        self.cert_path = os.path.join(ssl_dir, "dodo.crt")
        if not os.path.exists(ssl_dir):
            os.makedirs(ssl_dir)

    def generate(self):
        if not self.host:
            self.host = socket.gethostname()
        print "SSL Host used for Certificate Generation: "+self.host
        key = crypto.PKey()
        key.generate_key(crypto.TYPE_RSA, 2048)
        cert = crypto.X509()
        cert.get_subject().C = "IN"
        cert.get_subject().ST = "TN"
        cert.get_subject().L = "dodo"
        cert.get_subject().O = "dodo"
        cert.get_subject().OU = "dodo"
        cert.get_subject().CN = self.host
        cert.set_serial_number(1111)
        cert.gmtime_adj_notBefore(0)
        cert.gmtime_adj_notAfter(10 * 365 * 24 * 60 * 60)
        cert.set_issuer(cert.get_subject())
        cert.set_pubkey(key)
        cert.sign(key, "sha1")

        with open(self.cert_path, "w") as f:
            f.write(crypto.dump_certificate(crypto.FILETYPE_PEM, cert))

        with open(self.key_path, "w") as f:
            f.write(crypto.dump_privatekey(crypto.FILETYPE_PEM, key))

    def get(self):
        if not os.path.exists(self.cert_path) or not os.path.exists(self.cert_path):
            self.generate()
        # context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2) #For some reason android not able to communicate with stupid python on TLSv1_2
        context = ssl.SSLContext(ssl.PROTOCOL_SSLv23)
        context.load_cert_chain(self.cert_path, self.key_path)
        return context
