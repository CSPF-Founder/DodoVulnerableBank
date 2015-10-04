__author__ = 'CSPF'
from ..config import db

class User(db.Model):
    __tablename__ = "users"
    id = db.Column("id",db.Integer,primary_key=True,autoincrement=True)
    username = db.Column('username',db.String(20),unique=True,index=True)
    password = db.Column('password',db.String(50))

    def __init__(self,username,password):
        self.username = username
        self.password = password

    def is_authenticated(self):
        return True

    def is_active(self):
        return True

    def is_anonymous(self):
        return False


    def get_id(self):
        return unicode(self.id)

    def create(self):

        row = self.query.filter_by(username=self.username).first()
        if row:
            return False
        else:
            db.session.add(self)
            db.session.commit()
            return True

    def change_password(self,password):
        if self.query.filter_by(username=self.username).update(dict(password=self.password)):
            db.session.commit()
            return True
        else:
            return False

    def __repr__(self):
        return '<User %r>' % self.username
