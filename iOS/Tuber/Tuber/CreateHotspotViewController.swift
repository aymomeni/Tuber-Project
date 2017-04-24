//
//  CreateHotspotViewController.swift
//  Tuber
//
//  Created by Anne on 4/22/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class CreateHotspotViewController: UIViewController {

    @IBOutlet weak var topicTextArea: UITextView!
    @IBOutlet weak var locationTextArea: UITextView!
    
    // Set on HotspotInitalViewController
    var latitude: String!
    var longitude: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        topicTextArea!.layer.borderWidth = 1
        topicTextArea!.layer.borderColor = UIColor.lightGray.cgColor
        
        locationTextArea!.layer.borderWidth = 1
        locationTextArea!.layer.borderColor = UIColor.lightGray.cgColor
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func createButtonPressed(_ sender: Any) {
        // Set up the post request
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/createstudyhotspot"
        let requestURL = URL(string: server)
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let defaults = UserDefaults.standard
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let course = defaults.object(forKey: "selectedCourse") as! String
        let topic = topicTextArea.text
        let location = locationTextArea.text
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"\(course)\",\"topic\": \"\(topic!)\",\"latitude\":\"\(self.latitude!)\",\"longitude\":\"\(self.longitude!)\",\"locationDescription\":\"\(location!)\"}"
        
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
            
            if (r?.statusCode == 200 || r?.statusCode == 417)
            {
                do {
                    // Converting resonse to NSDictionary
                    let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                    
                    // Parsing the json
                    if let parseJSON = myJSON {

                        let parsed = parseJSON["hotspotID"] as! String
                        
                        // Call the segue
                        OperationQueue.main.addOperation{
                            self.performSegue(withIdentifier: "createHotspotSuccess", sender: parsed)
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
        // Executing the task
        task.resume()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "createHotspotSuccess"
        {
            let hotspot = sender as! String
            
            if let destination = segue.destination as? ActiveHotspotViewController
            {
                destination.hotspotID = hotspot
            }
        }
        
    }
}
