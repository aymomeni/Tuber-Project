//
//  MessageUsersListViewController.swift
//  Tuber
//
//  Created by Anne on 3/28/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit
import JSQMessagesViewController

class MessageUsersListViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    
    @IBOutlet weak var usersTableView: UITableView!
    
    var emails: [String] = []
    var firstNames: [String] = []
    var lastNames: [String] = []
    
    var messages = [JSQMessage]()

    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.usersTableView.delegate = self
        self.usersTableView.dataSource = self
        print(emails.count)
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return emails.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "messageUsersList", for: indexPath) as! MessageUsersListTableViewCell
        
        cell.userNameLabel.text = emails[indexPath.row]
        
//        cell.optionIconImageView.image = icons[indexPath.row]
//        cell.optionNameLabel.text = names[indexPath.row]
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        
        let currentCell = tableView.cellForRow(at: indexPath!)! as! MessageUsersListTableViewCell
        
        let selectedOption = currentCell.userNameLabel.text
        print(selectedOption)
        
//        if selectedOption == "Tutor Services"
//        {
//            performSegue(withIdentifier: "tutorServices", sender: selectedOption)
//            
//        }
//        else if selectedOption == "Study Hotspot"
//        {
//            performSegue(withIdentifier: "studyHotspot", sender: selectedOption)
//        }
//        else if selectedOption == "Offer To Tutor"
//        {
//            performSegue(withIdentifier: "offerToTutor", sender: selectedOption)
//        }
//        else if selectedOption == "Messaging"
//        {
//            performSegue(withIdentifier: "goToMessage", sender: selectedOption)
//        }
        
    }
    
    func prepConversation(email: String)
    {
        //created NSURL
        let requestURL = URL(string: "http://tuber-test.cloudapp.net/ProductRESTService.svc/getmessageconversation")
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let defaults = UserDefaults.standard
        
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"recipientEmail\":\"\(email)\"}"
        
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

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
