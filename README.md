# fingerprint-auth


<b>Add this code to initiate the authentication</b>
<br/><br/>
val intent = Intent(this, FingerprintAuthenticationActivity::class.java)<br/>
ActivityCompat.startActivityForResult(this, intent, Constants.REQUEST_CODE, null)

<b>Add this code to get the result</b>
<br/><br/>
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {<br/>
super.onActivityResult(requestCode, resultCode, data)<br/>
if (requestCode == Constants.REQUEST_CODE && resultCode == Activity.RESULT_OK) {<br/>
if (data != null) {<br/>
if (data.hasExtra(Constants.AUTH_STATUS) && data.hasExtra(Constants.AUTH_MESSAGE)) {<br/>
if (data.getBooleanExtra(Constants.AUTH_STATUS, false)) {<br/>
//Success<br/>
} else {<br/>
                  //Failed<br/>
                }<br/>
            }<br/>
        }<br/>
    }<br/>
}<br/>
