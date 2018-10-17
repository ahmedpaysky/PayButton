## PaySky Button SDK

The purpose of this SDK to help programmers to use PaySky payment SDK .

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

What things you need to install the software and how to install them

```
Create new Android project in Android Studio or if you have project already.
```

### Installing

A step by step that tell you how to get our SDK in your project.

```
1- open your android project.
2- in your project in build.gradle file in project level in allproject{} inside it you will find repositories{} inside it add :-
maven { url 'https://jitpack.io' }
Example:-
allprojects { repositories {
     maven { url 'https://jitpack.io' }
                           }
            }
3- in your build.gradle file in app level in dependencies{} add :- implementation 'com.github.payskyCompany:fabsdk:1.1.10'
Example:-
dependencies {
  implementation 'com.github.payskyCompany:paybutton:1.1.16'
}
4- Sync your project.

Note:- 1.1.16 may not be the last version check Releases in github to get latest version.
```
### Using SDK

```
in order to use our SDK you should get merchant id and Terminal id from our company.

1 – create a new instance from PayButton:-  
PayButton payButton = new PayButton(context);

you need to just pass some parameters to our button to know merchant data and amount this parameters is:-
  1-Merchat id
  2-Terminal id
  3-Payment amount
  4-Currency code
  5-merchant secure hash.
  
Note That:-
you shoud keep your secure hash and merchant id and terminal id with encryption before save them in storage if you want.

Example:-

payButton.setMerchantId(merchantId); // Merchant id
payButton.setTerminalId(terminalId); // Terminal  id
payButton.setPayAmount(amount); // Amount
payButton.setCurrencyCode(currencyCode); // Currency Code
payButton.setMerchantSecureHash("Merchant secure hash");

2 - in order to create transaction call:-

payButton.createTransaction(new PayButton.PaymentTransactionCallback() {

                    @Override
                    public void onCardTransactionSuccess(SuccessfulCardTransaction cardTransaction) {
                        paymentStatusTextView.setText(cardTransaction.toString());
                    }

                    @Override
                    public void onWalletTransactionSuccess(SuccessfulWalletTransaction walletTransaction) {
                        paymentStatusTextView.setText(walletTransaction.toString());
                    }

                    @Override
                    public void onError(Throwable error) {
                        paymentStatusTextView.setText("failed by:- " + error.getMessage());
                    }
                });

to create transaction in our sdk you just call createTransaction method and pass to it
PaymentTransactionCallback listener to call it after transaction.
this listener has 2 methods:-
  1 - onCardTransactionSuccess method
      this method called in case transaction success by card payment with SuccessfulCardTransaction object.
  2 - onWalletTransactionSuccess method 
      this method is called if customer make a wallet transaction with SuccessfulWalletTransaction object.
 3- onError method in case transaction failed with Throwable exception that has error info.
Example:- 

          payButton.createTransaction(new PayButton.PaymentTransactionCallback() {
                    @Override
                    public void onCardTransactionSuccess(SuccessfulCardTransaction cardTransaction) {
                        paymentStatusTextView.setText(cardTransaction.toString());
                    }

                    @Override
                    public void onWalletTransactionSuccess(SuccessfulWalletTransaction walletTransaction) {
                        paymentStatusTextView.setText(walletTransaction.toString());
                    }

                    @Override
                    public void onError(Throwable error) {
                        paymentStatusTextView.setText("failed by:- " + error.getMessage());
                    }
                });
            }
        });

```
## Deployment

1-Before deploy your project live ,you should get a merchant id and terminal id from our company .
2-you should keep your merchant id and terminal id secured
in your project, encrypt them before save them in project.

## Built With

* [Retrofit](http://square.github.io/retrofit/) - Android Networking library.
* [https://github.com/greenrobot/EventBus) - Event bus send events between your classes.


## Authors

**PaySky Company** - (https://www.paysky.io)

## Sample Project
**https://github.com/payskyCompany/payButton.git**



