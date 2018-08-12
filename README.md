# fingerprint-auth

<b>To start fingerprint scanner activity </b>

        val intent = Intent(this, FingerprintAuthenticationActivity::class.java)<br/>
        ActivityCompat.startActivityForResult(this, intent, Constants.REQUEST_CODE, null)

<b>To get the result</b>

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                super.onActivityResult(requestCode, resultCode, data)
                    if (requestCode == Constants.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        if (data.hasExtra(Constants.AUTH_STATUS) && data.hasExtra(Constants.AUTH_MESSAGE)) {
                            if (data.getBooleanExtra(Constants.AUTH_STATUS, false)) {
                                //Success
                            } else {
                                //Failed
                            }
                        }
                    }
                }
        }
