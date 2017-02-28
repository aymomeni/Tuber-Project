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
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        errorLabel.text = ""
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func loginButtonPress(_ sender: Any) {
        let sr = ServerRequest();
        var responseCode:Int;
        responseCode = -1;
        var JSON:NSDictionary?;
        
<<<<<<< HEAD
        //(responseCode,myJSON) = sr.verifyUser(email: emailTextField.text!, password:passwordTextField.text!);
        //sr.verifyUser(email: emailTextField.text!, password:passwordTextField.text!) //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"" + emailTextField.text! + "\",\"userPassword\":\"" + passwordTextField.text! + "\"}"
        let url = "http://tuber-test.cloudapp.net/ProductRESTService.svc/verifyuser"
        sr.runRequest(inputJSON: postParameters, server: url)
        {
            res,myJSON in
            JSON = myJSON;
            responseCode = res;
            if (responseCode == 200)
=======
        print("pressed login")
        
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
        
        print(postParameters)
        
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
>>>>>>> 05f976b7c4bed46406b29814678df36a8ae8647b
            {
                //parsing the json
                if let parseJSON = JSON {
                    
                    let defaults = UserDefaults.standard
                    
<<<<<<< HEAD
                    defaults.set(parseJSON["userEmail"] as! String?, forKey: "userEmail")
                    defaults.set(parseJSON["userStudentCourses"] as! Array<String>?, forKey: "userStudentCourses")
                    defaults.set(parseJSON["userToken"] as! String?, forKey: "userToken")
                    defaults.set(parseJSON["userTutorCourses"] as! Array<String>?, forKey: "userTutorCourses")
                    defaults.synchronize()
                    
                    print("Added to defaults")
                    
                    print(defaults.object(forKey: "userToken")!)
                    
                    OperationQueue.main.addOperation{
                        self.performSegue(withIdentifier: "loginSuccess", sender: nil)
=======
                        defaults.set(parseJSON["userEmail"] as! String?, forKey: "userEmail")
                        defaults.set(parseJSON["userStudentCourses"] as! Array<String>?, forKey: "userStudentCourses")
                        defaults.set(parseJSON["userToken"] as! String?, forKey: "userToken")
                        defaults.set(parseJSON["userTutorCourses"] as! Array<String>?, forKey: "userTutorCourses")
                        defaults.synchronize()

                        OperationQueue.main.addOperation{
                            self.performSegue(withIdentifier: "loginSuccess", sender: nil)
                        }
>>>>>>> 05f976b7c4bed46406b29814678df36a8ae8647b
                    }
                }
            }
<<<<<<< HEAD

        }
=======
            //rest of responses
            self.errorLabel.text = "\(r?.statusCode)"
            
        }
        //executing the task
        task.resume()
>>>>>>> 05f976b7c4bed46406b29814678df36a8ae8647b
    }
}
