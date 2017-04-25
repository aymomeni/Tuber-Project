//
//  ClassListViewController.swift
//  Tuber
//
//  Created by Anne on 12/6/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class ClassListViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, ButtonCellDelegate {
    
    @IBOutlet weak var classTableView: UITableView!
    
    var classes = UserDefaults.standard.object(forKey: "userStudentCourses") as! Array<String>
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        classTableView.tableFooterView = UIView(frame: .zero)
        self.view.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
//        self.view.backgroundColor = UIColor.lightGray
        self.classTableView.separatorStyle = .none
    }
    
    // Get rid of extra table cells
    override func viewDidAppear(_ animated: Bool) {
        classTableView.frame = CGRect(x: classTableView.frame.origin.x, y: classTableView.frame.origin.y, width: classTableView.frame.size.width, height: classTableView.contentSize.height)
    }
    
    // Get rid of extra table cells
    override func viewDidLayoutSubviews(){
        classTableView.frame = CGRect(x: classTableView.frame.origin.x, y: classTableView.frame.origin.y, width: classTableView.frame.size.width, height: classTableView.contentSize.height)
        classTableView.reloadData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return classes.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! ClassTableViewCell
        
        //Set cell properties
        cell.classNameLabel.text = classes[indexPath.row]
        cell.classNameLabel.font = UIFont(name: "HelveticaNeue", size: 28.0)!
        cell.messageButton.setImage(#imageLiteral(resourceName: "messaging"), for: .normal)
        cell.immediateButton.setImage(#imageLiteral(resourceName: "immediaterequest"), for: .normal)
        cell.scheduledButton.setImage(#imageLiteral(resourceName: "scheduletutor"), for: .normal)
        cell.hotspotButton.setImage(#imageLiteral(resourceName: "studyhotspot"), for: .normal)
        
        if cell.buttonDelegate == nil {
            cell.buttonDelegate = self
        }
        
        // Creates separation between cells
        cell.contentView.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
        let whiteRoundedView : UIView = UIView(frame: CGRect(x: 0, y: 10, width: self.view.frame.size.width - 35, height: 105))
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
        
        let currentCell = tableView.cellForRow(at: indexPath!)! as! ClassTableViewCell
        UserDefaults.standard.set(currentCell.classNameLabel.text! as String?, forKey: "selectedCourse")
        performSegue(withIdentifier: "studentOptions", sender: nil)
//        performSegue(withIdentifier: "shortcut", sender: nil)
    }
    
    /**
     * This fuction is called when a button in the table cell is called.  Allows the appropriate segue to be performed.
     */
    func cellTapped(cell: ClassTableViewCell, type: String) {
        
        let selectedCourse = classTableView.indexPath(for: cell)!.row
        UserDefaults.standard.set("\(classes[selectedCourse])", forKey: "selectedCourse")
        
        if (type == "Message"){
            loadMessageUsers()
        }
        else if (type == "Schedule"){
            performSegue(withIdentifier: "scheduleTutor", sender: nil)
        }
        else if (type == "Hotspot"){
            checkHotspotStatus()
        }
    }
    
    func checkHotspotStatus()
    {
        var hotspot = Bool()
        var hotspotID = String()
        var course = String()
        var topic = String()
        var location = String()
        var ownerString = String()
        var owner = Bool()
        
        // Set up the post request
        let requestURL = URL(string: "http://tuber-test.cloudapp.net/ProductRESTService.svc/userhotspotstatus")
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
                if let hotspotInfo = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary{
                    
                    print(hotspotInfo)
                    
                    if let arrJSON = (hotspotInfo as AnyObject)["hotspot"] as? NSDictionary{
                        hotspot = true
                        
                        hotspotID = arrJSON["hotspotID"] as! String
                        course = arrJSON["course"] as! String
                        topic = arrJSON["topic"] as! String
                        location = arrJSON["locationDescription"] as! String
                        ownerString = arrJSON["ownerEmail"] as! String
                        
                        if (ownerString == userEmail)
                        {
                            owner = true
                        }
                        else
                        {
                            owner = false
                        }
                    }
                    
                }
                else
                {
                    hotspot = false
                }
                
                OperationQueue.main.addOperation{
                    
                    if(hotspot)
                    {
                        // Set up the sender for the segue
                        var toSend = [String]()
                        
                        toSend.append(hotspotID)
                        toSend.append("You are in a \(course) hotspot.")
                        
                        if (owner)
                        {
                            toSend.append("deleteCurrent")
                        }
                        else
                        {
                            toSend.append("leave")
                        }
                        self.performSegue(withIdentifier: "activeHotspot", sender: toSend)
                    }
                    else
                    {
                        self.performSegue(withIdentifier: "studyHotspot", sender: nil)
                    }
                }
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
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "messages"
        {
            let appointmentInfo = sender as! [[String]]
            
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
        if segue.identifier == "activeHotspot"
        {
            let hotspotInfo = sender as! [String]
            
            if let destination = segue.destination as? ActiveHotspotViewController
            {
                destination.hotspotID = hotspotInfo[0]
                destination.messageContents = hotspotInfo[1]
                destination.pageSetup = hotspotInfo [2]
            }
        }
    }
    
}
