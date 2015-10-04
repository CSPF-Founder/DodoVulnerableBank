__author__ = 'CSPF'
import argparse
from os import path
def process_keyword_argument():
    parser = argparse.ArgumentParser()
    parser.add_argument('--ssl',
                        dest="ssl",
                        action="store_true",
                        help="Disable SSL Support"
                        )
    parser.add_argument('--any-ip',
                      dest='any_ip',
                      action="store_true",
                      help="To list on any IP address")

    parser.add_argument('--ssl-host',
                      dest='ssl_host',
                      help="Hostname to be used for generating the SSL Certificate")

    args = parser.parse_args()
    return args

def get_home_dir():
    return path.join(path.expanduser("~"),".dodo/")