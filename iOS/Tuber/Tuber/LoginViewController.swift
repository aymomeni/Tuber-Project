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
        
        let firebaseToken = UserDefaults.standard.object(forKey: "firebaseToken") as! String
        
        let postParameters = "{\"userEmail\":\"" + emailTextField.text! + "\",\"userPassword\":\"" + passwordTextField.text! + "\",\"firebaseToken\":\"\(firebaseToken)\"}";
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
                    
                    print(defaults.object(forKey: "userToken")!)
                    
                    OperationQueue.main.addOperation{
                        self.performSegue(withIdentifier: "loginSuccess", sender: nil)
                    }
                }
            }
            else if (responseCode == 401)
            {
                OperationQueue.main.addOperation{
                    let alertController = UIAlertController(title: "Login Failed", message:
                        "Incorrect Password", preferredStyle: UIAlertControllerStyle.alert)
                    alertController.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default,handler: nil))
                    self.present(alertController, animated: true, completion: nil)
                }
            }
            else
            {
                OperationQueue.main.addOperation{
                    let alertController = UIAlertController(title: "Login Failed", message:
                        "Could not access database.", preferredStyle: UIAlertControllerStyle.alert)
                    alertController.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default,handler: nil))
                    self.present(alertController, animated: true, completion: nil)
                }
            }
        }
    }
}
