__author__ = 'CSPF'

from . config import app,db_path,db
from model.Users import User
from model.Account import Account
from model.Transaction import Transaction
from os import stat,path


if not path.exists(db_path):
    #first time -default password
    db.create_all()
    user = User("mammoth","mammoth")
    user.id = 100000
    user.create()

    user1 = User("test","test")
    user1.create()

    account = Account(user.id,"13371111",100000)
    account.create()


    account1 = Account(user1.id,"13371112",400000)
    account1.create()

    first_transaction = Transaction(user.id,10000,13371111,13371112,remarks="Salary",transaction_details="User to User 1 (1000)")
    first_transaction.insert()


from . import controller