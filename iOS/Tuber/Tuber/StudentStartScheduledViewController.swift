//
//  StudentStartScheduledViewController.swift
//  
//
//  Created by Anne on 4/2/17.
//
//

import UIKit

class StudentStartScheduledViewController: UIViewController {

    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var durationLabel: UILabel!
    @IBOutlet weak var subjectLabel: UILabel!
    
    // Set on StudentViewScheduleTableViewController
    var date: String!
    var duration: String!
    var subject: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        dateLabel.text = date
        durationLabel.text = duration
        subjectLabel.text = subject
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func startSessionPressed(_ sender: Any) {
        // Set up the post request
        let requestURL = URL(string: "http://tuber-test.cloudapp.net/ProductRESTService.svc/startscheduledtutorsessionstudent")
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let defaults = UserDefaults.standard
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let course = defaults.object(forKey: "selectedCourse") as! String
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"\(course)\"}"
        
        print(postParameters)
        
        // Adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        // Creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                return;
            }
            
            let r = response as? HTTPURLResponse
            
            if (r?.statusCode == 200)
            {
                OperationQueue.main.addOperation{
                    self.performSegue(withIdentifier: "startSession", sender: "Success")
                }
            }
            else{
                OperationQueue.main.addOperation{
                    let alertController = UIAlertController(title: "Cannot Start Session", message:
                        "The tutor must start first.", preferredStyle: UIAlertControllerStyle.alert)
                    alertController.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default,handler: nil))
                    self.present(alertController, animated: true, completion: nil)
                }
            }
        }
        // Executing the task
        task.resume()
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
