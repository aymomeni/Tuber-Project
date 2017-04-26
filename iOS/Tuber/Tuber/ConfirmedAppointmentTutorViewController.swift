//
//  ConfirmedAppointmentTutorViewController.swift
//  Tuber
//
//  Created by Anne on 12/7/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class ConfirmedAppointmentTutorViewController: UIViewController {
    
    let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/"
    
    // Used for scheduledAppointments() and AppointmentRequests
    var studentNames: [[String]] = [[],[]]
    var dates: [[String]] = [[],[]]
    var durations: [[String]] = [[],[]]
    var topics: [[String]] = [[],[]]
    
    var labelContents: [String] = []

    @IBOutlet weak var button: UIButton!
    @IBOutlet weak var messageLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Set up the message based off of if the appointment has been confirmed or is finished.
        if (labelContents.count == 2)
        {
            messageLabel.text = "You earned $\(labelContents[1]) for your session with \(labelContents[0])"
        }
        
        self.view.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
        
        button.layer.cornerRadius = 5
        button.layer.borderWidth = 1
        
        var navArray:Array = (self.navigationController?.viewControllers)!
        if(navArray[navArray.count - 2] is UnconfirmedAppointmentTutorViewController)
        {
            navArray.remove(at: navArray.count - 2)
            navArray.remove(at: navArray.count - 2)
            self.navigationController?.viewControllers = navArray
        }
        else if (navArray[navArray.count - 2] is ActiveHotspotViewController)
        {
            navArray.remove(at: navArray.count - 2)
            navArray.remove(at: navArray.count - 2)
            navArray.remove(at: navArray.count - 2)
            
            self.navigationController?.viewControllers = navArray
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func viewSchedule(_ sender: Any) {
        scheduledAppointments()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "backToSchedule"
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
                    
                    self.performSegue(withIdentifier: "backToSchedule", sender: nil)
                }
                
                return;
                
            } catch {
                print(error)
            }
            
            
        }
        // Executing the task
        task.resume()
    }

}
