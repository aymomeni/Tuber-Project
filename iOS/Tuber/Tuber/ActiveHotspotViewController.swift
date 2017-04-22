//
//  ActiveHotspotViewController.swift
//  Tuber
//
//  Created by Anne on 2/6/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class ActiveHotspotViewController: UIViewController {
    
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var deletebutton: UIButton!
    
    var messageContents: String!
    var buttonText: String!
    var pageSetup: String!
    var hotspotID: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if (pageSetup != nil)
        {
            messageLabel.text = "Successfully Joined Hotspot"
            deletebutton.setTitle("Leave Hotspot", for: .normal)
        }
        
        if (hotspotID != nil)
        {
            print(hotspotID)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    @IBAction func deleteLeaveButton(_ sender: Any) {
        var server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/"
        
        let defaults = UserDefaults.standard
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        
        //creating the post parameter by concatenating the keys and values from text field
        var postParameters = String()
        
        if (messageLabel.text == "Successfully Created Hotspot")
        {
            server.append("deletestudyhotspot")
            postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"hotspotID\":\"\(hotspotID!)\"}"
        }
        else{
            server.append("leavestudyhotspot")
            postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\"}"
        }
        
        print (server)
        print(postParameters)
        
        //created NSURL
        let requestURL = URL(string: server)
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        print(postParameters)
        
        //creating a task to send the post request
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
                    self.performSegue(withIdentifier: "reloadMap", sender: nil)
                }
            }
            else{
                print(r?.statusCode as Any)
            }
            
        }
        //executing the task
        task.resume()
    }
    
}
