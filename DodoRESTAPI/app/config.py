__author__ = 'CSPF'

from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from os import path,makedirs,stat
from utils import get_home_dir

db_dir = path.join(get_home_dir(),"db")
if not path.exists(db_dir):
    #make sure to create home directory and database directory
    makedirs(db_dir)
db_path = path.join(db_dir,"Database.sqlite")

app = Flask("DodoBank")
app.config["SECRET_KEY"]="1234"
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///'+db_path

db = SQLAlchemy(app)