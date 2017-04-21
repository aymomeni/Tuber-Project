//
//  ClassOptionsViewController.swift
//  Tuber
//
//  Created by Anne on 12/7/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class ClassOptionsViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet weak var optionTableView: UITableView!
    
    //var passed: String!
    
    var icons = [#imageLiteral(resourceName: "tutorservices"), #imageLiteral(resourceName: "studyhotspot"), #imageLiteral(resourceName: "discussion"), #imageLiteral(resourceName: "messaging"), #imageLiteral(resourceName: "offertutor")]
    var names = ["Tutor Services", "Study Hotspot", "Discussion Forum", "Messaging", "Offer To Tutor"]
    
    var emails: [String] = []
    var firstNames: [String] = []
    var lastNames: [String] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()

         self.title = UserDefaults.standard.object(forKey: "selectedCourse") as? String
         
         self.navigationItem.hidesBackButton = true
         let newBackButton = UIBarButtonItem(title: "< Courses", style: UIBarButtonItemStyle.plain, target: self, action: #selector(ClassOptionsViewController.back(_:)))
         self.navigationItem.leftBarButtonItem = newBackButton
    }
    
    func back(_ sender: UIBarButtonItem) {
        _ = navigationController?.popToRootViewController(animated: true)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 5
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "classOptions", for: indexPath) as! ClassOptionsTableViewCell
        
        cell.optionIconImageView.image = icons[indexPath.row]
        cell.optionNameLabel.text = names[indexPath.row]
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        
        let currentCell = tableView.cellForRow(at: indexPath!)! as! ClassOptionsTableViewCell
        
        let selectedOption = currentCell.optionNameLabel.text
        
        if selectedOption == "Tutor Services"
        {
            performSegue(withIdentifier: "tutorServices", sender: selectedOption)
            
        }
        else if selectedOption == "Study Hotspot"
        {
            performSegue(withIdentifier: "studyHotspot", sender: selectedOption)
        }
        else if selectedOption == "Offer To Tutor"
        {
            performSegue(withIdentifier: "offerToTutor", sender: selectedOption)
        }
        else if selectedOption == "Messaging"
        {
            prepUserList()
            //            performSegue(withIdentifier: "messageUsers", sender: selectedOption)
        }
        
    }
    
    func prepUserList()
    {
        //created NSURL
        let requestURL = URL(string: "http://tuber-test.cloudapp.net/ProductRESTService.svc/getusers")
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let defaults = UserDefaults.standard
        
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\"}"
        
        print(postParameters)
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        //creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print("error is \(error)")
                return;
            }
            
            //parsing the response
            do {
                print(response)
                let hotspots = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                //self.returnedJSON = hotspots["studyHotspots"] as! [String : AnyObject]{
                if let arrJSON = hotspots["users"] {
                    if (arrJSON.count > 0) {
                        for index in 0...arrJSON.count-1 {
                            
                            let aObject = arrJSON[index] as! [String : AnyObject]
                            
                            print(aObject)
                            
                            
                            self.emails.append(aObject["email"] as! String)
                            self.firstNames.append(aObject["firstName"] as! String)
                            self.lastNames.append(aObject["lastName"] as! String)
                            
                        }
                    }
                }
                
                OperationQueue.main.addOperation{
                    
                    var toSend = [[String]]()
                    
                    toSend.append(self.emails)
                    toSend.append(self.firstNames)
                    toSend.append(self.lastNames)
                    
                    print(toSend.count)
                    
                    //                    print(toSend)
                    self.performSegue(withIdentifier: "messageUsers", sender: toSend)
                }
            } catch {
                print(error)
            }
            
        }
        //executing the task
        task.resume()
        
    }
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "messageUsers"
        {
            let appointmentInfo = sender as! [[String]]
            print(appointmentInfo[0])
            //            print(appointmentInfo[1])
            //            print(appointmentInfo[2])
            
            if let destination = segue.destination as? MessageUsersListViewController
            {
                destination.emails = []
                destination.firstNames = []
                destination.lastNames = []
                
                destination.emails = appointmentInfo[0]
                destination.firstNames = appointmentInfo[1]
                destination.lastNames = appointmentInfo[2]
                
                print("performing segue")
                //destination.passed = sender as? String
            }
        }
    }
}
