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
    @IBOutlet weak var confirmButton: UIButton!
    
    // Set on ScheduleTutorViewController
    var passed: [String]!
    
    let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/scheduletutor"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = UserDefaults.standard.object(forKey: "selectedCourse") as? String
        
        confirmLabel.text = passed[0]
        self.view.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
        confirmButton.layer.cornerRadius = 5
        confirmButton.layer.borderWidth = 1
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /**
     * This method sends the scheduled appointment request to the database.
     */
    @IBAction func confirmButtonPressed(_ sender: Any) {
        
        // Set up the post request
        let requestURL = URL(string: server)
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"

        
        // Adding the parameters to request body
        request.httpBody = passed[1].data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        // Creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print("error is \(error)")
                return;
            }
            
            let r = response as? HTTPURLResponse
            
            if (r?.statusCode == 200)
            {
                OperationQueue.main.addOperation{
                    self.performSegue(withIdentifier: "scheduleConfirmed", sender: "Success")
                }
            }
            else{
                print(r?.statusCode as Any)
            }
            
        }
        // Executing the task
        task.resume()
    }


}
