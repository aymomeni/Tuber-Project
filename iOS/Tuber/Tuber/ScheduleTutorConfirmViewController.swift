//
//  ScheduleTutorConfirmViewController.swift
//  Tuber
//
//  Created by Anne on 12/4/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class ScheduleTutorConfirmViewController: UIViewController {

    @IBOutlet weak var confirmLabel: UILabel!
    var passed: String!
    
    let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/scheduletutor"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = ClassListViewController.selectedClass.className

        confirmLabel.text = passed
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func confirmButtonPressed(_ sender: Any) {
        
        print(passed)
        //TODO: Add request to database
        
        //created NSURL
        let requestURL = NSURL(string: server)
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"

        
        //adding the parameters to request body
        request.httpBody = passed.data(using: String.Encoding.utf8)
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
                OperationQueue.main.addOperation{
                    self.performSegue(withIdentifier: "scheduleConfirmed", sender: "Success")
                }
            }
            else{
                print(r?.statusCode as Any)
            }
            //rest of responses
            //self.errorLabel.text = "errormessage"
            
        }
        //executing the task
        task.resume()
        
        //performSegue(withIdentifier: "scheduleConfirmed", sender: "hi")
        
    }


}
