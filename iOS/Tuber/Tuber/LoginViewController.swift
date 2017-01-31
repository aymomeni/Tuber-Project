//
//  LoginViewController.swift
//  Tuber
//
//  Created by Anne on 12/4/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {

    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var errorLabel: UILabel!
    
    //URL to our web service
    let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/verifyuser"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        errorLabel.text = ""
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func loginButtonPress(_ sender: Any) {
        
        //created NSURL
        let requestURL = NSURL(string: server)
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        //getting values from text fields
        let userEmail = emailTextField.text
        let userPassword = passwordTextField.text
        
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"" + userEmail! + "\",\"userPassword\":\"" + userPassword! + "\"}"
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        //creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print("error is \(error)")
                return;
            }
            
            let r = response as? HTTPURLResponse
            
            //parsing the response
            
            if (r?.statusCode == 200)
            {
                do {
                    //converting resonse to NSDictionary
                    let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                
                
                    //parsing the json
                    if let parseJSON = myJSON {
                    
                        let defaults = UserDefaults.standard
                    
                        defaults.set(parseJSON["userEmail"] as! String?, forKey: "userEmail")
                        defaults.set(parseJSON["userStudentCourses"] as! Array<String>?, forKey: "userStudentCourses")
                        defaults.set(parseJSON["userToken"] as! String?, forKey: "userToken")
                        defaults.set(parseJSON["userTutorCourses"] as! Array<String>?, forKey: "userTutorCourses")
                        defaults.synchronize()
                    
                        print("Added to defaults")
                    
                        print(defaults.object(forKey: "userToken")!)

                        OperationQueue.main.addOperation{
                            self.performSegue(withIdentifier: "loginSuccess", sender: nil)
                        }
                    }
                }
                catch {
                    print(error)
                }
            }
            //rest of responses
            self.errorLabel.text = "errormessage"
            
        }
        //executing the task
        task.resume()

        
        //TODO: check w/ database
        
//        if (emailTextField.text == "test" && passwordTextField.text == "test")
//        {
//            performSegue(withIdentifier: "loginSuccess", sender: nil)
//        }
//        else
//        {
//            errorLabel.text = "Invalid Login Credentials"
//        }
    }

}
