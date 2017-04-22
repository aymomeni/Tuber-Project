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
    
    var latitude: String!
    var longitude: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        print(latitude)
        print(longitude)
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    @IBAction func createButtonPressed(_ sender: Any) {
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/createstudyhotspot"
        
        //created NSURL
        let requestURL = URL(string: server)
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let defaults = UserDefaults.standard
        
        //getting values from text fields
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let course = defaults.object(forKey: "selectedCourse") as! String
        let topic = topicTextArea.text
        let location = locationTextArea.text
        
        print(topic)
        print(location)
        
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"\(course)\",\"topic\": \"\(topic!)\",\"latitude\":\"\(self.latitude!)\",\"longitude\":\"\(self.longitude!)\",\"locationDescription\":\"\(location!)\"}"
        print(postParameters)
        
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
            
            if (r?.statusCode == 200 || r?.statusCode == 417)
            {
                do {
                    //converting resonse to NSDictionary
                    let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                    
                    //parsing the json
                    if let parseJSON = myJSON {
                        
                        
                        
                        print(parseJSON["hotspotID"] as! String)
                        
                        let parsed = parseJSON["hotspotID"] as! String
                        
                        print(parsed)
                        //
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
        //executing the task
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
