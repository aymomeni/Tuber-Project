//
//  LoginViewController.swift
//  Tuber
//
//  Created by Anne on 12/4/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit
import Parse
import Bolts

class LoginViewController: UIViewController {

    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var errorLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        errorLabel.text = ""
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func loginButtonPress(_ sender: Any) {
        
        //TODO: check w/ database
        
        if (emailTextField.text == "test" && passwordTextField.text == "test")
        {
            performSegue(withIdentifier: "loginSuccess", sender: nil)
        }
        else
        {
            errorLabel.text = "Invalid Login Credentials"
        }
    }

}
