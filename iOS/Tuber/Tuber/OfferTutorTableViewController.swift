//
//  OfferTutorTableViewController.swift
//  Tuber
//
//  Created by Anne on 12/7/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class OfferTutorTableViewController: UITableViewController {
    
    let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/"
    
    // Cell Items
    var icons = [#imageLiteral(resourceName: "immediaterequest"), #imageLiteral(resourceName: "scheduletutor"), #imageLiteral(resourceName: "messaging")]
    var names = ["Immediate Service", "View Schedule", "Messaging"]
    
    // Used in scheduledAppointments() and appointmentRequests()
    var studentNames: [[String]] = [[],[]]
    var dates: [[String]] = [[],[]]
    var durations: [[String]] = [[],[]]
    var topics: [[String]] = [[],[]]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = UserDefaults.standard.object(forKey: "selectedCourse") as? String
        self.navigationController?.navigationBar.isTranslucent = false
        self.view.backgroundColor = UIColor.lightGray
        self.tableView.separatorStyle = .none
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return icons.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "tutorServices", for: indexPath) as! OfferTutorTableViewCell
        
        cell.optionImage.image = icons[indexPath.row]
        cell.optionLabel.text = names[indexPath.row]
        
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
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        
        let currentCell = tableView.cellForRow(at: indexPath!)! as! OfferTutorTableViewCell
        
        let selectedOption = currentCell.optionLabel.text
        
        if selectedOption == "Immediate Service"
        {
            performSegue(withIdentifier: "immediateRequest", sender: selectedOption)
        }
        else if selectedOption == "View Schedule"
        {
            scheduledAppointments()
        }
        else if selectedOption == "Messaging"
        {
            loadMessageUsers()
        }
        
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "tutorViewSchedule"
        {
            if let destination = segue.destination as? TutorViewScheduleTableViewController
            {
                destination.students.removeAll()
                destination.dates.removeAll()
                destination.duration.removeAll()
                destination.subjects.removeAll()
                
                destination.students = self.studentNames
                destination.dates = self.dates
                destination.duration = self.durations
                destination.subjects = self.topics
            }
            
        }
        else if segue.identifier == "messages"
        {
            let appointmentInfo = sender as! [[String]]
            print(appointmentInfo[0])
            
            if let destination = segue.destination as? MessageUsersListViewController
            {
                destination.emails = []
                destination.firstNames = []
                destination.lastNames = []
                
                destination.emails = appointmentInfo[0]
                destination.firstNames = appointmentInfo[1]
                destination.lastNames = appointmentInfo[2]
            }
        }
    }
    
    /**
     * This fuction accesses the database to find the tutor's accepted scheduled appointments.
     */
    func scheduledAppointments()
    {
        // Set up the post request
        let requestURL = URL(string: server + "findallscheduletutoracceptedrequests")
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let userEmail = UserDefaults.standard.object(forKey: "userEmail") as! String
        let userToken = UserDefaults.standard.object(forKey: "userToken") as! String
        let course = UserDefaults.standard.object(forKey: "selectedCourse") as! String
        let postParameters = "{\"userEmail\":\"" + userEmail + "\",\"userToken\":\"" + userToken + "\",\"course\":\"" + course + "\"}"
        
        // Adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        // Creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                return;
            }
            
            // Parsing the response
            do {
                let appointments = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                self.studentNames = [[],[]]
                self.dates = [[],[]]
                self.durations = [[],[]]
                self.topics = [[],[]]
                
                if let arrJSON = appointments["tutorRequestItems"] {
                    print(arrJSON.count)
                    if (arrJSON.count > 0)
                    {
                        for index in 0...arrJSON.count-1 {
                            
                            let aObject = arrJSON[index] as! [String : AnyObject]
                            
                            self.dates[0].append(aObject["dateTime"] as! String)
                            self.durations[0].append(aObject["duration"] as! String)
                            self.studentNames[0].append(aObject["studentEmail"] as! String)
                            self.topics[0].append(aObject["topic"] as! String)
                        }
                        
                    }
                }

                OperationQueue.main.addOperation{
                    self.appointmentRequests()
                }
                
                return;
                
            } catch {
                print(error)
            }
        }
        // Executing the task
        task.resume()
    }
    
    /**
     * This fuction accesses the database to find scheduled appointment requests.
     */
    func appointmentRequests()
    {
        // Set up the post request
        let requestURL = URL(string: server + "findallscheduletutorrequests")
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let userEmail = UserDefaults.standard.object(forKey: "userEmail") as! String
        let userToken = UserDefaults.standard.object(forKey: "userToken") as! String
        let course = UserDefaults.standard.object(forKey: "selectedCourse") as! String
        let postParameters = "{\"userEmail\":\"" + userEmail + "\",\"userToken\":\"" + userToken + "\",\"course\":\"" + course + "\"}"
        
        // Adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        // Creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                return;
            }
            
            let r = response as? HTTPURLResponse
            
            // Parsing the response
            do {
                let appointments = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                if let arrJSON = appointments["tutorRequestItems"] {
                    print(arrJSON.count)
                    if (arrJSON.count > 0)
                    {
                        for index in 0...arrJSON.count-1 {
                            
                            let aObject = arrJSON[index] as! [String : AnyObject]
                            
                            self.dates[1].append(aObject["dateTime"] as! String)
                            self.durations[1].append(aObject["duration"] as! String)
                            self.studentNames[1].append(aObject["studentEmail"] as! String)
                            self.topics[1].append(aObject["topic"] as! String)
                            
                        }
                    }
                }
                OperationQueue.main.addOperation{
                    self.performSegue(withIdentifier: "tutorViewSchedule", sender: nil)
                }
                
                return;
                
            } catch {
                print(error)
            }
            
            
        }
        // Executing the task
        task.resume()
    }
    
    /**
     * This fuction accesses the database to load all of the users for the message list
     */
    func loadMessageUsers() {
        
        var emails: [String] = []
        var firstNames: [String] = []
        var lastNames: [String] = []
        
        // Set up the post request
        let requestURL = URL(string: "http://tuber-test.cloudapp.net/ProductRESTService.svc/getusers")
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let defaults = UserDefaults.standard
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\"}"
        
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
                let messageUsers = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                if let arrJSON = messageUsers["users"] {
                    if (arrJSON.count > 0) {
                        for index in 0...arrJSON.count-1 {
                            
                            let aObject = arrJSON[index] as! [String : AnyObject]
                            
                            emails.append(aObject["email"] as! String)
                            firstNames.append(aObject["firstName"] as! String)
                            lastNames.append(aObject["lastName"] as! String)
                            
                        }
                    }
                }
                
                OperationQueue.main.addOperation{
                    
                    // Set up the sender for the segue
                    var toSend = [[String]]()
                    
                    toSend.append(emails)
                    toSend.append(firstNames)
                    toSend.append(lastNames)
                    
                    self.performSegue(withIdentifier: "messages", sender: toSend)
                }
            } catch {
                print(error)
            }
            
        }
        // Executing the task
        task.resume()
    }
    
}
