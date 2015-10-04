__author__ = 'CSPF'
import socket
from app.utils import process_keyword_argument
from app import app

if __name__ == "__main__":
    args = process_keyword_argument()
    server = "127.0.0.1"

    ssl_host = socket.gethostname()
    if args.ssl_host:
        ssl_host = args.ssl_host

    if args.any_ip:
        server = "0.0.0.0"

    if args.ssl:
        from app.digital_certificate import SSLCertifiacate
        app.run(host=server, port=6060, ssl_context=SSLCertifiacate(host=ssl_host).get(), debug=True)
    else:
        app.run(host=server, port=6060, debug=True)
