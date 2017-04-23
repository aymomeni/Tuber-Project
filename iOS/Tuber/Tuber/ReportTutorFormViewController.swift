//
//  ReportTutorFormViewController.swift
//  Tuber
//
//  Created by Anne on 3/28/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class ReportTutorFormViewController: UIViewController {
    
    // Set on ReportTutorSessionsTableViewController
    var tempPostParameters: String = ""

    @IBOutlet weak var messageContents: UITextView!
    
    override func viewDidLoad() {
        
        super.viewDidLoad()
        
        messageContents!.layer.borderWidth = 1
        messageContents!.layer.borderColor = UIColor.lightGray.cgColor

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func reportButtonPressed(_ sender: Any) {
        
        // Set up the post request
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/reporttutor";
        let requestURL = NSURL(string: server)
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let postParameters = "\(tempPostParameters)\"\(messageContents.text! as String)\"}"
        
        // Adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        // Creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print("error is \(error)")
                return;
            }
  
            OperationQueue.main.addOperation{
                self.performSegue(withIdentifier: "sendReport", sender: nil)
            }
            
        }
        // Executing the task
        task.resume()
    }

}
