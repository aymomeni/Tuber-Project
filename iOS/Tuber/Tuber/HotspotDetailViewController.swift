//
//  HotspotDetailViewController.swift
//  Tuber
//
//  Created by Anne on 3/8/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class HotspotDetailViewController: UIViewController {

    var hotspotID: String!
    var memberList: String!
    @IBOutlet weak var memberListTextView: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        memberListTextView.text = memberList
    }
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    @IBAction func joinButtonPress(_ sender: Any) {
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/joinstudyhotspot"
        
        //created NSURL
        let requestURL = URL(string: server)
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let defaults = UserDefaults.standard
        
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"hotspotID\":\"\(hotspotID as String)\"}"
        
        print(postParameters)
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        //creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print(error as! String)
                return;
            }
            
            let r = response as? HTTPURLResponse
            
            if (r?.statusCode == 200)
            {
                OperationQueue.main.addOperation{
                    self.performSegue(withIdentifier: "joinHotspot", sender: nil)
                }
            }
            else{
                print(r?.statusCode as Any)
            }
        }
        //executing the task
        task.resume()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "viewHotspotDetail"
        {
            
            if let destination = segue.destination as? ActiveHotspotViewController
            {
                destination.messageLabel.text = "Successfully Joined Hotspot"
                destination.deletebutton.setTitle("Leave Hotspot", for: .normal)
            }
        }
    }

}
