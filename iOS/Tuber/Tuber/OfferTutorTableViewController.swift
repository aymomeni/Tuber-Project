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
    
    var studentNames: [[String]] = [[],[]]
    var dates: [[String]] = [[],[]]
    var durations: [[String]] = [[],[]]
    var topics: [[String]] = [[],[]]

    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
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
        return 2
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        
        print("row is \(indexPath?.row)")
        
        if (indexPath?.row == 1)
        {
            scheduledAppointments()
        }
//        if (indexPath?.row == 1){
//            print("first list")
//            print(studentNames)
//            
//            scheduledAppointments(){ status, error in
//                if status != nil {
//                    print("second list")
//                    print(self.studentNames)
//                    
//                    self.appointmentRequests() {status2, error2 in
//                        if status2 != nil {
//                            print("third list")
//                            print(self.studentNames)
//                            
//                            if let destination = segue.destination as? TutorViewScheduleTableViewController
//                            {
//                                destination.students = self.studentNames
//                                destination.dates = self.dates
//                                destination.duration = self.durations
//                                destination.subjects = self.topics
//                                //destination.passed = sender as? String
//                            }
//                        }
//                    }
//                }
//            }
//        
//            performSegue(withIdentifier: "tutorViewSchedule", sender: selectedOption)
//        }
    
//        let currentCell = tableView.cellForRow(at: indexPath!)! as! TutorServicesTableViewCell
//        
//        
//        let selectedOption = currentCell.optionNameLabel.text
//        
//        if selectedOption == "Immediate Request"
//        {
//            performSegue(withIdentifier: "immediateRequest", sender: selectedOption)
//            
//        }
//        else if selectedOption == "Schedule Tutor"
//        {
//            performSegue(withIdentifier: "scheduleTutor", sender: selectedOption)
//            
//        }
//        
    }

    /*
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "reuseIdentifier", for: indexPath)

        // Configure the cell...

        return cell
    }
    */

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
    
        override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
            if segue.identifier == "tutorViewSchedule"
            {
                print("prep for segue")
//                print("first list")
//                print(studentNames)
//                
//                scheduledAppointments(){ status, error in
//                    if status != nil {
//                        print("second list")
//                        print(self.studentNames)
//                        
//                        self.appointmentRequests() {status2, error2 in
//                            if status2 != nil {
//                                print("third list")
//                                print(self.studentNames)
                
                                if let destination = segue.destination as? TutorViewScheduleTableViewController
                                {
                                    destination.students = self.studentNames
                                    destination.dates = self.dates
                                    destination.duration = self.durations
                                    destination.subjects = self.topics
                                    //destination.passed = sender as? String
                                    print("destinations set")
                                }
//                            }
//                        }
//                    }
//                }
            }
        }

//    func scheduledAppointments(completionHandler: @escaping (String?, NSError?) -> Void)
    func scheduledAppointments()
    {
        
//        let semaphore = DispatchSemaphore(value: 0)
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
//                completionHandler(nil, error as NSError?)
                return;
            }
            
//            semaphore.signal();
            
//            let r = response as? HTTPURLResponse
            
            //parsing the response
            do {
                //print(response)
                let hotspots = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                //self.returnedJSON = hotspots["studyHotspots"] as! [String : AnyObject]{
                if let arrJSON = hotspots["tutorRequestItems"] {
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
//                completionHandler("complete", nil)
                OperationQueue.main.addOperation{
                    
                    print(self.studentNames)
                    self.appointmentRequests()
                }
                
                return;
                
            } catch {
                print(error)
            }
        }
        //executing the task
        task.resume()
//        semaphore.wait(timeout: .distantFuture);
    }
    
//    func appointmentRequests(completionHandler: @escaping (String?, NSError?) -> Void)
    func appointmentRequests()
    {
//        let semaphore = DispatchSemaphore(value: 0)
        
        
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
        
        print(postParameters)
        
        //creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
//                completionHandler(nil, error as NSError?)
                return;
            }
            
//            semaphore.signal();
            
            let r = response as? HTTPURLResponse
            
            //parsing the response
            do {
                //print(response)
                let hotspots = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                //self.returnedJSON = hotspots["studyHotspots"] as! [String : AnyObject]{
                if let arrJSON = hotspots["tutorRequestItems"] {
                    print(arrJSON.count)
                    if (arrJSON.count > 0)
                    {
                    for index in 0...arrJSON.count-1 {
                        
                        let aObject = arrJSON[index] as! [String : AnyObject]
                        
                        //print(aObject)
                        
                        
                        self.dates[1].append(aObject["dateTime"] as! String)
                        self.durations[1].append(aObject["duration"] as! String)
                        self.studentNames[1].append(aObject["studentEmail"] as! String)
                        self.topics[1].append(aObject["topic"] as! String)
                        
                    }
                    }
                }
//                completionHandler("complete", nil)
                OperationQueue.main.addOperation{
                    
                    print(self.studentNames)
                    self.performSegue(withIdentifier: "tutorViewSchedule", sender: nil)
                }
                
                return;
                
            } catch {
                print(error)
            }
            
            
        }
        //executing the task
        task.resume()
//        semaphore.wait(timeout: .distantFuture);
    }


}
