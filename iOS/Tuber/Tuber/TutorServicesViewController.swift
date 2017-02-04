//
//  TutorServicesViewController.swift
//  Tuber
//
//  Created by Anne on 12/7/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class TutorServicesViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet weak var servicesTableView: UITableView!
    
    let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/"
    
    var studentNames: [[String]] = []
    var dates: [[String]] = []
    var durations: [[String]] = []
    var topics: [[String]] = []


    var icons = [#imageLiteral(resourceName: "immediaterequest"), #imageLiteral(resourceName: "scheduletutor"), #imageLiteral(resourceName: "viewschedule")]
    var names = ["Immediate Request", "Schedule Tutor", "View Schedule"]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = ClassListViewController.selectedClass.className
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 3
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "tutorServices", for: indexPath) as! TutorServicesTableViewCell
        
        cell.optionIconImageView.image = icons[indexPath.row]
        cell.optionNameLabel.text = names[indexPath.row]
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        
        let currentCell = tableView.cellForRow(at: indexPath!)! as! TutorServicesTableViewCell
        
        let selectedOption = currentCell.optionNameLabel.text
        
        if selectedOption == "Immediate Request"
        {
            performSegue(withIdentifier: "immediateRequest", sender: selectedOption)
            
        }
        else if selectedOption == "Schedule Tutor"
        {
            performSegue(withIdentifier: "scheduleTutor", sender: selectedOption)
            
        }
        
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "tutorViewSchedule"
        {
            scheduledAppointments()
            appointmentRequests()
            
            if let destination = segue.destination as? TutorViewScheduleTableViewController
            {
                destination.students = studentNames
                destination.dates = dates
                destination.duration = durations
                destination.subjects = topics
                //destination.passed = sender as? String
            }
        }
    }
    
    func scheduledAppointments()
    {
        //created NSURL
        let requestURL = NSURL(string: server + "findallscheduletutoracceptedrequests")
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let userEmail = UserDefaults.standard.object(forKey: "userEmail") as! String
        let userToken = UserDefaults.standard.object(forKey: "userToken") as! String
        let course = UserDefaults.standard.object(forKey: "selectedCourse") as! String
        
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"" + userEmail + "\",\"userToken\":\"" + userToken + "\",\"course\":\"" + course + "\"}"
        
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
            
            let r = response as? HTTPURLResponse
            
            //parsing the response
            do {
                //print(response)
                let hotspots = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                //self.returnedJSON = hotspots["studyHotspots"] as! [String : AnyObject]{
                if let arrJSON = hotspots["tutorRequestItems"] {
                    for index in 0...arrJSON.count-1 {
                        
                        let aObject = arrJSON[index] as! [String : AnyObject]
                        
                        self.dates[0].append(aObject["dateTime"] as! String)
                        self.durations[0].append(aObject["duration"] as! String)
                        self.studentNames[0].append(aObject["studentEmail"] as! String)
                        self.topics[0].append(aObject["topic"] as! String)
                        
//                        print(aObject["hotspotID"] as! String)
//                        print(aObject["student_count"] as! String)
                    }
                }

            } catch {
                print(error)
            }
            
        }
        //executing the task
        task.resume()
    }
    
    func appointmentRequests()
    {
        //created NSURL
        let requestURL = NSURL(string: server + "findallscheduletutorrequests")
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let userEmail = UserDefaults.standard.object(forKey: "userEmail") as! String
        let userToken = UserDefaults.standard.object(forKey: "userToken") as! String
        let course = UserDefaults.standard.object(forKey: "selectedCourse") as! String
        
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"" + userEmail + "\",\"userToken\":\"" + userToken + "\",\"course\":\"" + course + "\"}"
        
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
            
            let r = response as? HTTPURLResponse
            
            //parsing the response
            do {
                //print(response)
                let hotspots = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                //self.returnedJSON = hotspots["studyHotspots"] as! [String : AnyObject]{
                if let arrJSON = hotspots["tutorRequestItems"] {
                    for index in 0...arrJSON.count-1 {
                        
                        let aObject = arrJSON[index] as! [String : AnyObject]
                        
                        //print(aObject)
                        
                        
                        self.dates[1].append(aObject["dateTime"] as! String)
                        self.durations[1].append(aObject["duration"] as! String)
                        self.studentNames[1].append(aObject["studentEmail"] as! String)
                        self.topics[1].append(aObject["topic"] as! String)
                        
                    }
                }

                
                //self.tableView.reloadData()
                //                //converting resonse to NSDictionary
                //                let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                //
                //                //parsing the json
                //                if let parseJSON = myJSON {
                //
                //                    //creating a string
                //                    var msg : String!
                //
                //                    //getting the json response
                //                    msg = parseJSON["message"] as! String?
                //
                //                    //printing the response
                //                    print(msg)
                //
                //                }
            } catch {
                print(error)
            }
            
//            if (r?.statusCode == 200)
//            {
//                OperationQueue.main.addOperation{
//                    self.performSegue(withIdentifier: "scheduleConfirmed", sender: "Success")
//                }
//            }
//            else{
//                print(r?.statusCode as Any)
//            }
            
        }
        //executing the task
        task.resume()
    }

}
