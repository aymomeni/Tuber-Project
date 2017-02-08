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
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // get the current date and time
        let currentDateTime = Date()
        
        // initialize the date formatter and set the style
        let formatter = DateFormatter()
        formatter.timeStyle = .short
        formatter.dateStyle = .short
        
        
        startTimeLabel.text = formatter.string(from: currentDateTime)
        
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func prepare(for segue: UIStoryboardSegue, sender: [String]) {
        if segue.identifier == "endSession"
        {
            print("prep for segue")
            
            if let destination = segue.destination as? ConfirmedAppointmentTutorViewController
            {
//                let message = "You earned $\(sender[0]) for your session with \(sender[1])"
//                destination.labelContents = message

                destination.labelContents = "Completed Scheduled Tutor Session"
                //destination.passed = sender as? String
                print(destination.labelContents)
                print("destinations set")
            }
            
        }
    }

    @IBAction func stopSession(_ sender: Any) {
        
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/endtutorsession"
        
        //created NSURL
        let requestURL = NSURL(string: server)
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let defaults = UserDefaults.standard
        
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let course = defaults.object(forKey: "selectedCourse") as! String
        
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"\(course)\"}"
        
        print(postParameters)
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        //creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                //                completionHandler(nil, error as NSError?)
                return;
            }
            
            //            semaphore.signal();
            
            let r = response as? HTTPURLResponse
            
            if (r?.statusCode == 200 || r?.statusCode == 417)
            {
                do {
                    //converting resonse to NSDictionary
                    let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                    
                    
                    //parsing the json
                    if let parseJSON = myJSON {
                        
                        var parsed = [String]()
                        parsed.append("Completed Scheduled Tutor Session")
                        
//                        print(parseJSON["studentEmail"] as! String)
//                        print(parseJSON["sessionCost"] as! String)
                        
//                        parsed.append((parseJSON["studentEmail"] as! String?)!)
//                        parsed.append((parseJSON["sessionCost"] as! String?)!)
                        
                        print(parsed)
                        
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
                print(r?.statusCode as Any)
            }
        }
        //executing the task
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
