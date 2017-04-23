//
//  ReportTutorListTableViewController.swift
//  Tuber
//
//  Created by Anne on 3/2/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class ReportTutorListTableViewController: UITableViewController {
    
    var tutorFirstNames: [String] = []
    var tutorLastNames: [String] = []
    var tutorEmails: [String] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = "Report Tutor"
        
        self.view.backgroundColor = UIColor.lightGray
        self.tableView.separatorStyle = .none

        // Uncomment the following line to preserve selection between presentations
//         self.clearsSelectionOnViewWillAppear = true

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
        return tutorFirstNames.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
     let cell = tableView.dequeueReusableCell(withIdentifier: "reportTutorList", for: indexPath) as! ReportTutorListTableViewCell
     
     cell.tutorNameLabel.text = tutorFirstNames[indexPath.row] + " " + tutorLastNames[indexPath.row]
        
        //Creates separation between cells
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
        
//        let currentCell = tableView.cellForRow(at: indexPath!)! as! ReportTutorListTableViewCell
        
//        let selectedOption = currentCell.tutorNameLabel.text
        
        prepTutorSessions(tutorFirstNames[(indexPath?.row)!], tutorLastName: tutorLastNames[(indexPath?.row)!], tutorEmail: tutorEmails[(indexPath?.row)!])
//        prepTutorSessions(name: selectedOption!, tutorEmail: tutorEmails[(indexPath?.row)!])        prepTutorSessions(tutorFirstName: tutorFirstNames[(indexPath?.row)!], tutorLastName: tutorLastNames[(indexPath?.row)!], tutorEmail: tutorEmails[(indexPath?.row)!])        
    }
    
    func prepTutorSessions(_ tutorFirstName: String, tutorLastName: String, tutorEmail: String)
    {
        
        var sessionStartTime: [String] = []
        var sessionID: [String] = []
        
        let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/reporttutorgetsessionlist";
        
        //created NSURL
        let requestURL = URL(string: server)
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let defaults = UserDefaults.standard
        let email = defaults.object(forKey: "userEmail") as? String
        let token = defaults.object(forKey: "userToken") as? String
        
        let postParameters = "{\"userEmail\":\"" + email! + "\",\"userToken\":\"" + token! + "\",\"tutorEmail\":\"" + tutorEmail + "\",\"tutorFirstName\":\"" + tutorFirstName + "\",\"tutorLastName\":\"" + tutorLastName + "\"}";
        
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        print(postParameters)
        
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
                if let arrJSON = hotspots["tutorList"] {
                    if (arrJSON.count > 0) {
                        for index in 0...arrJSON.count-1 {
                            
                            let aObject = arrJSON[index] as! [String : AnyObject]
                            
                            print(aObject)
                            
                    
                            sessionStartTime.append(aObject["sessionStartTime"] as! String)
                            sessionID.append(aObject["tutorSessionID"] as! String)
                            
                        }
                    }
                }
//                print(self.tutorNames)
                print(self.tutorEmails)
                
                OperationQueue.main.addOperation{
                    
                    let params = "{\"userEmail\":\"" + email! + "\",\"userToken\":\"" + token! + "\",\"tutorEmail\":\"" + tutorEmail
                    var toSend = [[String]]()
//                    toSend.append(self.tutorNames)
                    toSend.append([params])
                    toSend.append(sessionStartTime)
                    toSend.append(sessionID)
                    
                    print(toSend.count)
                    
                    //                    print(toSend)
                    self.performSegue(withIdentifier: "viewTutorSessions", sender: toSend)
                }
            } catch {
                print(error)
            }
            
        }
        //executing the task
        task.resume()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "viewTutorSessions"
        {
            let appointmentInfo = sender as! [[String]]
            print(appointmentInfo[0])
            print(appointmentInfo[1])
            print(appointmentInfo[2])
            
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

}
