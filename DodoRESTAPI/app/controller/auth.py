__author__ = 'CSPF'
from flask_login import LoginManager,login_user
from flask import request,jsonify

from ..config import app
from .. model.Users import User

login_manager = LoginManager()
login_manager.init_app(app)

@login_manager.user_loader
def user_loader(id):
    return User.query.get(id)

@app.route("/login",methods=['POST',"GET"])
def login():
    username = request.form['username']
    password = request.form['password']
    valid_user = User.query.filter_by(username=username,password=password).first()
    if valid_user is None:
        return jsonify({"success":False})
    else:
        login_user(valid_user,remember=True)
        return jsonify({"success":True,"id":valid_user.id})