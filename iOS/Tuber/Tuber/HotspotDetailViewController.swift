//
//  HotspotDetailViewController.swift
//  Tuber
//
//  Created by Anne on 3/8/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class HotspotDetailViewController: UIViewController {
    
    @IBOutlet weak var memberListTextView: UITextView!
    
    @IBOutlet weak var joinHotspotButton: UIButton!
    // Set on HotspotInitialViewController
    var hotspotID: String!
    var memberList: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        memberListTextView.text = memberList
        joinHotspotButton.backgroundColor = UIColor.darkGray
        joinHotspotButton.layer.cornerRadius = 5
        joinHotspotButton.layer.borderWidth = 1
        
        self.view.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /**
     * This method tell the database that the user is joining a hotspot.
     */
    @IBAction func joinButtonPress(_ sender: Any) {
        
        // Set up the post request
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/joinstudyhotspot"
        let requestURL = URL(string: server)
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let defaults = UserDefaults.standard
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let course = defaults.object(forKey: "selectedCourse") as! String
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"\(course)\",\"hotspotID\":\"\(hotspotID as String)\"}"
        
        //Adding the parameters to request body
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
            
            if (r?.statusCode == 200)
            {
                OperationQueue.main.addOperation{
                    self.performSegue(withIdentifier: "joinHotspot", sender: "leave")
                }
            }
            else{
                print(r?.statusCode as Any)
            }
        }
        // Executing the task
        task.resume()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "joinHotspot"
        {
            if let destination = segue.destination as? ActiveHotspotViewController
            {
                destination.pageSetup = sender as! String
                destination.messageContents = "Successfully Joined Hotspot"
                destination.hotspotID = nil
            }
        }
    }
    
}
