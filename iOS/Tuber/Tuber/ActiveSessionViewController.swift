//
//  ActiveSessionViewController.swift
//  Tuber
//
//  Created by Anne on 2/6/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class ActiveSessionViewController: UIViewController {

    @IBOutlet weak var startTimeLabel: UILabel!
    @IBOutlet weak var stopButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Get the current date and time
        let currentDateTime = Date()
        
        // Initialize the date formatter and set the style
        let formatter = DateFormatter()
        formatter.timeStyle = .short
        formatter.dateStyle = .short
        
        startTimeLabel.text = formatter.string(from: currentDateTime)
        
        self.view.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
        
        stopButton.layer.cornerRadius = 5
        stopButton.layer.borderWidth = 1
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        print("in prep")
        if segue.identifier == "endSession"
        {
            print("prep for segue")
            
            if let destination = segue.destination as? ConfirmedAppointmentTutorViewController
            {
                destination.labelContents = sender as! [String]

                print(destination.labelContents)
                print("destinations set")
            }
            
        }
    }

    /**
     * This method stops an active tutoring session.
     */
    @IBAction func stopSession(_ sender: Any) {
        
        // Set up the post request
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/endtutorsession"
        let requestURL = URL(string: server)
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let defaults = UserDefaults.standard
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let course = defaults.object(forKey: "selectedCourse") as! String
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"\(course)\"}"
        
        // Adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        // Creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print(error as! String)
                return;
            }
            
            let r = response as? HTTPURLResponse
            
            if (r?.statusCode == 200 || r?.statusCode == 417)
            {
                do {
                    // Converting resonse to NSDictionary
                    let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                    
                    // Parsing the json
                    if let parseJSON = myJSON {
                        
                        var parsed = [String]()
                        
                        parsed.append((parseJSON["studentEmail"] as! String?)!)
                        parsed.append("\(parseJSON["sessionCost"] as! Double)")

                        OperationQueue.main.addOperation{
                            self.performSegue(withIdentifier: "endSession", sender: parsed)
                        }
                    }
                }
                catch {
                    print(error)
                }
            }
            else{
                OperationQueue.main.addOperation{
                    let alertController = UIAlertController(title: "Cannot End Session", message:
                        "The student did not start the session.", preferredStyle: UIAlertControllerStyle.alert)
                    alertController.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default,handler: nil))
                    self.present(alertController, animated: true, completion: nil)
                }
            }
        }
        // Executing the task
        task.resume()

        
    }
}
