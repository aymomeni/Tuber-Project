import UIKit

class LoginViewController: UIViewController
{
    
    
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var errorLabel: UILabel!
    @IBOutlet weak var loginButton: UIButton!
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        errorLabel.text = ""
    }
    
    
    override func didReceiveMemoryWarning()
    {
        super.didReceiveMemoryWarning()    }
    
    @IBAction func loginButtonPress(_ sender: Any)
    {
        let sr = ServerRequest();
        var responseCode:Int;
        responseCode = -1;
        var JSON:NSDictionary?;
        let postParameters = "{\"userEmail\":\"" + emailTextField.text! + "\",\"userPassword\":\"" + passwordTextField.text! + "\",\"firebaseToken\":\"" + "" + "\"}";
        let url = "http://tuber-test.cloudapp.net/ProductRESTService.svc/verifyuser";
        
        sr.runRequest(inputJSON: postParameters, server: url)
        {
            res,myJSON in
            JSON = myJSON;
            responseCode = res;
            if (responseCode == 200)
            {
                //parsing the json
                if let parseJSON = JSON {
                    
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
         
        }
    }
}
