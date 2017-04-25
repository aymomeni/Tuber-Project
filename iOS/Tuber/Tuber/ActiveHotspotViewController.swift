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
    
    // Set on HotspotInitialViewCotroller or CreateHotspotViewController
    var messageContents: String!
    var buttonText: String!
    var pageSetup: String!
    var hotspotID: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Set up the page to delete or leave a hotspot
        if (pageSetup != nil)
        {
            if (pageSetup == "leave")
            {
                messageLabel.text = messageContents
                deletebutton.setTitle("Leave Hotspot", for: .normal)
            }
            else if (pageSetup == "deleteCurrent")
            {
                messageLabel.text = messageContents
            }
        }
        
        deletebutton.backgroundColor = UIColor.darkGray
        deletebutton.layer.cornerRadius = 5
        deletebutton.layer.borderWidth = 1
        self.view.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /**
     * This method will let the user leave a hotspot or delete a hotspot if they are the owner.
     */
    @IBAction func deleteLeaveButton(_ sender: Any) {
        
        // Create the URL and post parameters
        var server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/"
        
        let defaults = UserDefaults.standard
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        var postParameters = String()
        
//        if (messageLabel.text == "Successfully Created Hotspot")
        if (pageSetup != nil && pageSetup == "leave")
        {
            server.append("leavestudyhotspot")
            postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\"}"
        }
        else{
            server.append("deletestudyhotspot")
            postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"hotspotID\":\"\(hotspotID!)\"}"
        }
        
        // Set up the post request
        let requestURL = URL(string: server)
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
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
        // Executing the task
        task.resume()
    }
    
}
