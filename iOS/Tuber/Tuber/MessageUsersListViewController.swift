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
    
    //Set on previous screen
    var emails: [String] = []
    var firstNames: [String] = []
    var lastNames: [String] = []
    
    var messages = [JSQMessage]()

    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = "Messages"
        
        self.usersTableView.delegate = self
        self.usersTableView.dataSource = self
        
        self.view.backgroundColor = UIColor.lightGray
        self.usersTableView.separatorStyle = .none
        self.navigationController?.navigationBar.isTranslucent = false
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
        
        cell.userNameLabel.text = firstNames[indexPath.row] + " " + lastNames[indexPath.row]
        
        // Creates separation between cells
        cell.contentView.backgroundColor = UIColor.lightGray
        let whiteRoundedView : UIView = UIView(frame: CGRect(x: 10, y: 10, width: self.view.frame.size.width - 20, height: 70))
        whiteRoundedView.layer.backgroundColor = CGColor(colorSpace: CGColorSpaceCreateDeviceRGB(), components: [1.0, 1.0, 1.0, 1.0])
        whiteRoundedView.layer.masksToBounds = false
        whiteRoundedView.layer.cornerRadius = 3.0
        whiteRoundedView.layer.shadowOffset = CGSize(width: -1, height: 1)
        whiteRoundedView.layer.shadowOpacity = 0.5
        cell.contentView.addSubview(whiteRoundedView)
        cell.contentView.sendSubview(toBack: whiteRoundedView)
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        
        let currentCell = tableView.cellForRow(at: indexPath!)! as! MessageUsersListTableViewCell
        
        let selectedOption = currentCell.userNameLabel.text
        
        prepConversation(email: emails[(indexPath?.row)!], name: selectedOption!)
    }
    
    /**
     * This method loads a conversation between the current user and another user.  The two parameters are stirings for the recipient's email and name.
     */
    func prepConversation(email: String, name: String)
    {
        // Set up the post request
        let requestURL = URL(string: "http://tuber-test.cloudapp.net/ProductRESTService.svc/getmessageconversation")
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let defaults = UserDefaults.standard
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"recipientEmail\":\"\(email)\"}"
        
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
            
            // Parsing the response
            do {
                let dbmessages = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                var messages = [JSQMessage]()
                
                if let arrJSON = dbmessages["messages"] {
                    if (arrJSON.count > 0) {
                        for index in 0...arrJSON.count-1 {

                            let aObject = arrJSON.objectAt(index) as! [String : AnyObject]
                            
                            messages.append(JSQMessage(senderId: aObject["fromEmail"] as! String, displayName: aObject["fromEmail"] as! String, text: aObject["message"] as! String))
                            
                        }
                    }
                }
                
                // Set up the sender for the segue
                OperationQueue.main.addOperation{
                    
                    var toPass = MessageInfo()
                    toPass.recipient = email
                    toPass.messages = messages
                    
                    self.performSegue(withIdentifier: "goToMessage", sender: toPass)
                }
            } catch {
                print(error)
            }
            
        }
        // Executing the task
        task.resume()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToMessage"
        {
            let params = sender as! MessageInfo
            
            if let destination = segue.destination as? MessageWindowViewController
            {
                destination.recipientEmail = ""
                destination.messages = [JSQMessage]()
                
                destination.recipientEmail = params.recipient
                destination.messages = params.messages
            }
        }
    }

}

/**
 * This struct is used as the goToMessageSegue sender.
 */
struct MessageInfo {
    var recipient = ""
    var messages = [JSQMessage]()
}
