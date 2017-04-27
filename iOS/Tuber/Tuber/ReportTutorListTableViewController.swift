//
//  ReportTutorListTableViewController.swift
//  Tuber
//
//  Created by Anne on 3/2/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class ReportTutorListTableViewController: UITableViewController {
    
    // Set on TutorServicesViewController
    var tutorFirstNames: [String] = []
    var tutorLastNames: [String] = []
    var tutorEmails: [String] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = "Report Tutor"
        
        self.view.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
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
        return tutorFirstNames.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "reportTutorList", for: indexPath) as! ReportTutorListTableViewCell
     
        cell.tutorNameLabel.text = tutorFirstNames[indexPath.row] + " " + tutorLastNames[indexPath.row]
        
        //Creates separation between cells
        cell.contentView.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
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
        
        prepTutorSessions(tutorFirstNames[(indexPath?.row)!], tutorLastName: tutorLastNames[(indexPath?.row)!], tutorEmail: tutorEmails[(indexPath?.row)!])
    }
    
    /**
     * This method loads all the sessions between the user and  the selected tutor.
     */
    func prepTutorSessions(_ tutorFirstName: String, tutorLastName: String, tutorEmail: String)
    {
        
        var sessionStartTime: [String] = []
        var sessionID: [String] = []
        
        // Set up the post request
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/reporttutorgetsessionlist"
        let requestURL = URL(string: server)
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // // Create the post parameters
        let defaults = UserDefaults.standard
        let email = defaults.object(forKey: "userEmail") as? String
        let token = defaults.object(forKey: "userToken") as? String
        let postParameters = "{\"userEmail\":\"" + email! + "\",\"userToken\":\"" + token! + "\",\"tutorEmail\":\"" + tutorEmail + "\",\"tutorFirstName\":\"" + tutorFirstName + "\",\"tutorLastName\":\"" + tutorLastName + "\"}";
        
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
                let tutors = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                if let arrJSON = tutors["tutorList"] {
                    if (arrJSON.count > 0) {
                        for index in 0...arrJSON.count-1 {
                            
                            let aObject = arrJSON[index] as! [String : AnyObject]

                            sessionStartTime.append(aObject["sessionStartTime"] as! String)
                            sessionID.append(aObject["tutorSessionID"] as! String)
                            
                        }
                    }
                }
                
                // Set up the sender for the segue
                OperationQueue.main.addOperation{
                    
                    let params = "{\"userEmail\":\"" + email! + "\",\"userToken\":\"" + token! + "\",\"tutorEmail\":\"" + tutorEmail
                    
                    var toSend = [[String]]()
                    toSend.append([params])
                    toSend.append(sessionStartTime)
                    toSend.append(sessionID)
                    
                    self.performSegue(withIdentifier: "viewTutorSessions", sender: toSend)
                }
            } catch {
                print(error)
            }
            
        }
        // Executing the task
        task.resume()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "viewTutorSessions"
        {
            let appointmentInfo = sender as! [[String]]
            
            if let destination = segue.destination as? ReportTutorSessionsTableViewController
            {
                destination.postParameters = []
                destination.sessionStartTime = []
                destination.sessionID = []
                
                destination.postParameters = appointmentInfo[0]
                destination.sessionStartTime = appointmentInfo[1] 
                destination.sessionID = appointmentInfo[2] 
            }
        }
    }
    
}
