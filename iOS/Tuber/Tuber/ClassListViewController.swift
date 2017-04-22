//
//  ClassListViewController.swift
//  Tuber
//
//  Created by Anne on 12/6/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class ClassListViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, CAPSPageMenuDelegate, ButtonCellDelegate {
    
    @IBOutlet weak var classTableView: UITableView!
    
    var pageMenu : CAPSPageMenu?
    
    var classes = UserDefaults.standard.object(forKey: "userStudentCourses") as! Array<String>
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        classTableView.tableFooterView = UIView(frame: .zero)
        self.view.backgroundColor = UIColor.lightGray
        self.classTableView.separatorColor = UIColor.lightGray
    }
    
    override func viewDidAppear(_ animated: Bool) {
        classTableView.frame = CGRect(x: classTableView.frame.origin.x, y: classTableView.frame.origin.y, width: classTableView.frame.size.width, height: classTableView.contentSize.height)
        
    }
    
    override func viewDidLayoutSubviews(){
        classTableView.frame = CGRect(x: classTableView.frame.origin.x, y: classTableView.frame.origin.y, width: classTableView.frame.size.width, height: classTableView.contentSize.height)
        classTableView.reloadData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        //TODO: DB query, how many classes enrolled
        return classes.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! ClassTableViewCell
        
        //TODO: DB query,
        cell.classNameLabel.text = classes[indexPath.row]
        cell.classNameLabel.font = UIFont(name: "HelveticaNeue", size: 28.0)!
        cell.messageButton.setImage(#imageLiteral(resourceName: "messaging"), for: .normal)
        cell.immediateButton.setImage(#imageLiteral(resourceName: "immediaterequest"), for: .normal)
        cell.scheduledButton.setImage(#imageLiteral(resourceName: "scheduletutor"), for: .normal)
        cell.hotspotButton.setImage(#imageLiteral(resourceName: "studyhotspot"), for: .normal)
        
        if cell.buttonDelegate == nil {
            cell.buttonDelegate = self
        }
        
        cell.contentView.backgroundColor = UIColor.lightGray
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
    }
    
    // MARK: - ButtonCellDelegate
    
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
            performSegue(withIdentifier: "studyHotspot", sender: nil)
        }
    }
    
    func loadMessageUsers() {
        
        var emails: [String] = []
        var firstNames: [String] = []
        var lastNames: [String] = []
        
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
                
                if let arrJSON = hotspots["users"] {
                    if (arrJSON.count > 0) {
                        for index in 0...arrJSON.count-1 {
                            
                            let aObject = arrJSON[index] as! [String : AnyObject]
                            
                            print(aObject)
                            
                            
                            emails.append(aObject["email"] as! String)
                            firstNames.append(aObject["firstName"] as! String)
                            lastNames.append(aObject["lastName"] as! String)
                            
                        }
                    }
                }
                
                OperationQueue.main.addOperation{
                    
                    var toSend = [[String]]()
                    
                    toSend.append(emails)
                    toSend.append(firstNames)
                    toSend.append(lastNames)
                    
                    print(toSend.count)
                    
                    self.performSegue(withIdentifier: "messages", sender: toSend)
                }
            } catch {
                print(error)
            }
            
        }
        //executing the task
        task.resume()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "messages"
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
    
}
