__author__ = 'CSPF'
from ..config import db


class Transaction(db.Model):
    __tablename__ = "transaction"
    id = db.Column("id", db.Integer, primary_key=True, autoincrement=True)
    customer_id = db.Column("customer_id", db.Integer)
    amount = db.Column('amount', db.Integer)
    remarks = db.Column('remarks', db.String(100))
    transaction_details = db.Column('transaction_details', db.String(200))
    sender_account_number = db.Column('sender_accoutn_number', db.String(50))
    receiver_account_number = db.Column('receiver_account_number', db.String(50))

    def __init__(self, customer_id, amount, sender_account_number, receiver_account_number, remarks="",
                 transaction_details=""):
        self.customer_id = customer_id
        self.amount = amount
        self.sender_account_number = sender_account_number
        self.receiver_account_number = receiver_account_number
        self.remarks = remarks
        self.transaction_details = transaction_details

    def get_id(self):
        return unicode(self.id)

    def insert(self):
        db.session.add(self)
        db.session.commit()
        return True

    def __repr__(self):
        return '<Transaction %r>' % self.id
