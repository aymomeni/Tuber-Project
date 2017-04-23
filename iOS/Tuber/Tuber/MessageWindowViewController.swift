//
//  MessageWindowViewController.swift
//  Tuber
//
//  Created by Anne on 3/28/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit
import JSQMessagesViewController

class MessageWindowViewController: JSQMessagesViewController {

    // Set on MessageUsersListTableViewController
    var messages = [JSQMessage]()
    var userEmail = ""
    var recipientEmail = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()

        let defaults = UserDefaults.standard
        userEmail = defaults.object(forKey: "userEmail") as! String
        
        self.senderId = userEmail
        self.senderDisplayName = userEmail
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func didPressSend(_ button: UIButton!, withMessageText text: String!, senderId: String!, senderDisplayName: String!, date: Date!) {
        print("\(text)")
        
        sendMessage(messagetext: text)
        messages.append(JSQMessage(senderId: self.senderId, displayName: self.senderDisplayName, text: text))
        collectionView.reloadData()
        finishSendingMessage()
    }
    
    override func didPressAccessoryButton(_ sender: UIButton!) {
        print("pressed accessory button")
    }

    override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return messages.count
    }
    
    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = super.collectionView(collectionView, cellForItemAt: indexPath) as! JSQMessagesCollectionViewCell
        return cell
    }
    
    override func collectionView(_ collectionView: JSQMessagesCollectionView!, messageDataForItemAt indexPath: IndexPath!) -> JSQMessageData! {
        return messages[indexPath.item]
    }
    
    override func collectionView(_ collectionView: JSQMessagesCollectionView!, messageBubbleImageDataForItemAt indexPath: IndexPath!) -> JSQMessageBubbleImageDataSource! {
        let bubbleFactory = JSQMessagesBubbleImageFactory()
        
        let currentMessage = messages[indexPath.row]
        if (currentMessage.senderId == self.senderId)
        {
            return bubbleFactory?.outgoingMessagesBubbleImage(with: .blue)
        }
        else{
            return bubbleFactory?.incomingMessagesBubbleImage(with: .green)
        }
        
    }
    
    override func collectionView(_ collectionView: JSQMessagesCollectionView!, avatarImageDataForItemAt indexPath: IndexPath!) -> JSQMessageAvatarImageDataSource! {
        return nil
    }
    
    /**
     * This method adds a sent message to the database.  The parameter is the string message being sent.
     */
    func sendMessage(messagetext : String)
    {
        // Set up the post request
        let requestURL = URL(string: "http://tuber-test.cloudapp.net/ProductRESTService.svc/sendmessage")
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let defaults = UserDefaults.standard
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"recipientEmail\":\"\(self.recipientEmail)\",\"message\":\"\(messagetext)\"}"
        
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
                return;
            }
        }
        // Executing the task
        task.resume()
    }

}
