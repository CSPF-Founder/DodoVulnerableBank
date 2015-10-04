__author__ = 'CSPF'
from ..config import db

class Account(db.Model):
    __tablename__ = "accounts"
    id = db.Column("id",db.Integer,primary_key=True)
    branch = db.Column('branch',db.String(50))
    account_number = db.Column('account_number',db.String(50))
    balance = db.Column('balance',db.Integer)

    def __init__(self,id,account_number,balance=0,branch="Arkham"):
        self.id = id
        self.account_number = account_number
        self.balance = balance
        self.branch = branch


    def get_id(self):
        return unicode(self.id)

    def create(self):

        row = self.query.filter_by(id=self.id).first()
        if row:
            return False
        else:
            db.session.add(self)
            db.session.commit()
            return True

    def debit(self,debit_amount):
        new_amount = self.balance - int(debit_amount)
        if self.query.filter_by(id=self.id).update(dict(balance=new_amount)):
            db.session.commit()
            print "New Balance : "+str(self.balance)+"\n"

    def credit(self,debit_amount):
        new_amount = self.balance + int(debit_amount)
        if self.query.filter_by(id=self.id).update(dict(balance=new_amount)):
            db.session.commit()
            print "New Balance : "+str(self.balance)+"\n"

    def __repr__(self):
        return '<Account %r>' % self.account_number
